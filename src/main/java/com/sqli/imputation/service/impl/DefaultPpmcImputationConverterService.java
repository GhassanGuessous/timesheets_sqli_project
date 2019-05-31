package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.*;
import com.sqli.imputation.security.SecurityUtils;
import com.sqli.imputation.service.PpmcImputationConverterService;
import com.sqli.imputation.service.dto.CollabExcelImputationDTO;
import com.sqli.imputation.service.util.FileExtensionUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultPpmcImputationConverterService implements PpmcImputationConverterService {

    private static final int HEADER_INDEX = 0;
    private static final int FIRST_LINE_INDEX = 1;
    private static final String OUT_OF_OFFICE = "Out of Office";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String RESOURCE_USER_NAME = "Resource User Name";
    private static final String PROJECT_REQUEST_MISC = "Project/Request/Misc";
    private static final String DAY = "Day";
    private static final String ACTUAL_EFFORT_MAN_DAY = "Actual Effort (Man/Day)";
    private static final String WEEKLY_ACTUAL_EFFORT_COLUMN = "Weekly Actual Effort";
    private static final String MONTH = "Month";
    private static final String YEAR = "Year";

    private final Logger log = LoggerFactory.getLogger(DefaultPpmcImputationConverterService.class);

    @Autowired
    private ImputationConverterUtilService imputationConverterUtilService;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;

    @Override
    public Optional<Imputation> getPpmcImputationFromExcelFile(MultipartFile file, Team team) {
        try {
            InputStream excelFile = file.getInputStream();
            Optional<Sheet> sheet = getWeeklyActualEffortSheet(file.getOriginalFilename(), excelFile);
            Map<String, Integer> headerColumns = getHeaderColumns(sheet.get());

            Set<String> collaboratorsPpmcIds = getCollaborators(sheet.get(), headerColumns);
            List<CollabExcelImputationDTO> excelImputationDTOS = getExcelCollabDTOS(sheet.get(), headerColumns);

            Imputation imputation = createPpmcImputation(sheet.get(), headerColumns);
            createDailyImputationsForEachCollab(collaboratorsPpmcIds, excelImputationDTOS, imputation);

            getImputationsGivenConnectedUser(imputation, team);
            imputationConverterUtilService.sortImputations(imputation);

            excelFile.close();
            return Optional.of(imputation);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Sheet> getWeeklyActualEffortSheet(String originalFileName, InputStream excelFile) throws IOException {
        String extension = FileExtensionUtil.getExtension(originalFileName);
        return getRows(excelFile, extension);
    }

    private Optional<Sheet> getRows(InputStream excelFile, String excelExtension) throws IOException {
        if(FileExtensionUtil.isXLS(excelExtension)){
            HSSFWorkbook workbook = new HSSFWorkbook(excelFile);
            return Optional.of(workbook.getSheet(WEEKLY_ACTUAL_EFFORT_COLUMN));
        }
        if(FileExtensionUtil.isXLSX(excelExtension)){
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            return Optional.of(workbook.getSheet(WEEKLY_ACTUAL_EFFORT_COLUMN));
        }
        return Optional.empty();
    }

    private Map<String, Integer> getHeaderColumns(Sheet sheet) {
        //headerColumnMap contains name of the header of each column and its index in the file
        Map<String, Integer> headerColumnsMap = new HashMap<>();
        Row row = sheet.getRow(HEADER_INDEX);
        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
            Cell cell = row.getCell(j);
            if(cell != null) headerColumnsMap.put(cell.getStringCellValue(), j);
        }
        return headerColumnsMap;
    }

    private Set<String> getCollaborators(Sheet sheet, Map<String, Integer> headerColumns) {
        Set<String> collaborators = new HashSet<>();
        //I'm ignoring header for that I have +1 in loop
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (j == headerColumns.get(RESOURCE_USER_NAME) && !cell.getStringCellValue().isEmpty()) {
                    collaborators.add(cell.getStringCellValue());
                }
            }
        }
        return collaborators;
    }

    private List<CollabExcelImputationDTO> getExcelCollabDTOS(Sheet sheet, Map<String, Integer> headerColumns) {
        List<CollabExcelImputationDTO> excelImputationDTOS = new ArrayList<>();
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            CollabExcelImputationDTO dto = new CollabExcelImputationDTO();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                buildCollabExcelDTO(dto, j, cell, headerColumns);
            }
            excelImputationDTOS.add(dto);
        }
        return ignoreOutOfOfficeImputations(excelImputationDTOS);
    }

    private void buildCollabExcelDTO(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Map<String, Integer> headerColumns) {
        setCollaborator(dto, currentElementColumnIndex, cell, headerColumns.get(RESOURCE_USER_NAME));
        setProjectRequestMisc(dto, currentElementColumnIndex, cell, headerColumns.get(PROJECT_REQUEST_MISC));
        setDay(dto, currentElementColumnIndex, cell, headerColumns.get(DAY));
        setCharge(dto, currentElementColumnIndex, cell, headerColumns.get(ACTUAL_EFFORT_MAN_DAY));
    }

    private void setCollaborator(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer idPpmcColumnIndex) {
        if (currentElementColumnIndex == idPpmcColumnIndex) {
            dto.setCollaborator(cell.getStringCellValue());
        }
    }

    private void setProjectRequestMisc(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer requestMiscColumnIndex) {
        if(currentElementColumnIndex == requestMiscColumnIndex){
            dto.setProjectRequestMisc(cell.getStringCellValue());
        }
    }

    private void setDay(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer dayColumnIndex) {
        if(currentElementColumnIndex == dayColumnIndex){
            dto.setDay((int) cell.getNumericCellValue());
        }
    }

    private void setCharge(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer manDayColumnIndex) {
        if(currentElementColumnIndex == manDayColumnIndex){
            dto.setCharge(cell.getNumericCellValue());
        }
    }

    private List<CollabExcelImputationDTO> ignoreOutOfOfficeImputations(List<CollabExcelImputationDTO> excelImputationDTOS) {
        return excelImputationDTOS.stream().filter(
            collabExcelImputationDTO -> !collabExcelImputationDTO.getProjectRequestMisc().equals(OUT_OF_OFFICE)).collect(Collectors.toList()
        );
    }

    private Imputation createPpmcImputation(Sheet rows, Map<String, Integer> headerColumns) {
        ImputationType imputationType = imputationConverterUtilService.findImputationTypeByNameLike(Constants.PPMC_IMPUTATION_TYPE);
        Map<String, Integer> period = getPeriod(rows, headerColumns);
        return imputationConverterUtilService.createImputation(period.get(YEAR), period.get(MONTH), imputationType);
    }

    private Map<String, Integer> getPeriod(Sheet sheet, Map<String, Integer> headerColumns) {
        Map<String, Integer> period = new HashMap<>();
        int month = (int) sheet.getRow(FIRST_LINE_INDEX).getCell(headerColumns.get(MONTH)).getNumericCellValue();
        int year = (int) sheet.getRow(FIRST_LINE_INDEX).getCell(headerColumns.get(YEAR)).getNumericCellValue();
        period.put(MONTH, month);
        period.put(YEAR, year);
        return period;
    }

    private void createDailyImputationsForEachCollab(Set<String> ppmcIDs, List<CollabExcelImputationDTO> excelImputationDTOS, Imputation imputation) {
        Set<CollaboratorMonthlyImputation> monthlyImputations = new HashSet<>();
        ppmcIDs.forEach(ppmcId -> {
            Optional<Correspondence> correspondenceOptional = correspondenceRepository.findByIdPPMC(ppmcId).stream().findFirst();
            if(!correspondenceOptional.isPresent()){
                log.error("NO PPMC id for "+ppmcId);
                return;
            }else{
                Collaborator collaborator = correspondenceOptional.get().getCollaborator();
                CollaboratorMonthlyImputation monthlyImputation = imputationConverterUtilService.createMonthlyImputation(imputation, collaborator);
                Set<CollaboratorDailyImputation> dailyImputations = new HashSet<>();

                createDailyImputationFromExcelImputations(excelImputationDTOS, ppmcId, monthlyImputation, dailyImputations);

                monthlyImputation.setDailyImputations(dailyImputations);
                setTotalOfMonthlyImputation(monthlyImputation);
                monthlyImputations.add(monthlyImputation);
            }
        });
        imputation.setMonthlyImputations(monthlyImputations);
    }

    private boolean isCorrespondenceNotExist(Correspondence correspondence) {
        return correspondence == null;
    }

    private void createDailyImputationFromExcelImputations(
        List<CollabExcelImputationDTO> excelImputationDTOS, String ppmcId,
        CollaboratorMonthlyImputation monthlyImputation, Set<CollaboratorDailyImputation> dailyImputations
    ) {
        excelImputationDTOS.forEach(dto -> {
            if(dto.getCollaborator().equals(ppmcId)){
                if(imputationConverterUtilService.isDailyImputationExist(dailyImputations, dto.getDay())){
                    setChargeToExistingDailyImputation(dailyImputations, dto.getDay(), dto.getCharge());
                }else{
                    CollaboratorDailyImputation dailyImputation = imputationConverterUtilService.createDailyImputation(dto.getDay(), dto.getCharge(), monthlyImputation);
                    dailyImputations.add(dailyImputation);
                }
            }
        });
    }

    private void setChargeToExistingDailyImputation(Set<CollaboratorDailyImputation> dailyImputations, int day, double charge) {
        double oldCharge = dailyImputations.stream().filter(
            daily -> daily.getDay().equals(day)
        ).findFirst().get().getCharge();
        dailyImputations.stream().filter(daily -> daily.getDay().equals(day)).findFirst().get().setCharge(oldCharge + charge);
    }

    private void setTotalOfMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation) {
        monthlyImputation.getDailyImputations().forEach(daily -> monthlyImputation.setTotal(monthlyImputation.getTotal() + daily.getCharge())
        );
    }

    private void getImputationsGivenConnectedUser(Imputation imputation, Team team) {
        if(SecurityUtils.isCurrentUserInRole(ROLE_ADMIN)){
            imputation.setMonthlyImputations(getAssinedCollabsOnly(imputation.getMonthlyImputations()));
        } else {
            imputation.setMonthlyImputations(getTeamMembersOnly(imputation.getMonthlyImputations(), team));
        }
    }

    private Set<CollaboratorMonthlyImputation> getTeamMembersOnly(Set<CollaboratorMonthlyImputation> monthlyImputations, Team team) {
        return monthlyImputations.stream().filter(monthlyImputation ->
            monthlyImputation.getCollaborator().getTeam() != null
                && monthlyImputation.getCollaborator().getTeam().getId().equals(team.getId())
        ).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<CollaboratorMonthlyImputation> getAssinedCollabsOnly(Set<CollaboratorMonthlyImputation> monthlyImputations) {
        return monthlyImputations.stream().filter(monthlyImputation ->
            monthlyImputation.getCollaborator().getTeam() != null
        ).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
