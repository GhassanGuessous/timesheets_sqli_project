package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.*;
import com.sqli.imputation.security.SecurityUtils;
import com.sqli.imputation.service.PpmcImputationConverterService;
import com.sqli.imputation.service.TeamService;
import com.sqli.imputation.service.dto.CollabExcelImputationDTO;
import com.sqli.imputation.service.util.FileExtensionUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    private static final String PPMC_TYPE_NAME = "PPMC";
    private static final String OUT_OF_OFFICE = "Out of Office";
    private static final String ROLE_DELCO = "ROLE_DELCO";
    private static final String RESOURCE_USER_NAME = "Resource User Name";
    private static final String PROJECT_REQUEST_MISC = "Project/Request/Misc";
    private static final String DAY = "Day";
    private static final String ACTUAL_EFFORT_MAN_DAY = "Actual Effort (Man/Day)";
    private static final String WEEKLY_ACTUAL_EFFORT_COLUMN = "Weekly Actual Effort";
    private static final String MONTH = "Month";
    private static final String YEAR = "Year";

    @Autowired
    private ImputationConverterUtilService imputationConverterUtilService;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;

    private Map<String, Integer> headerColumns = new HashMap<>();

    @Override
    public Optional<Imputation> getPpmcImputationFromExcelFile(MultipartFile file) {
        try {
            InputStream excelFile = file.getInputStream();
            Optional<Sheet> sheet = getWeeklyActualEffortSheet(file, excelFile);
            headerColumns = getHeaderCoumns(sheet.get());

            Set<String> collaborators = getCollaborators(sheet.get(), headerColumns);
            List<CollabExcelImputationDTO> excelImputationDTOS = getExcelCollabDTOS(sheet.get(), headerColumns);

            Imputation imputation = createPpmcImputation(sheet.get(), headerColumns);
            createDailyImputationsForEachCollab(collaborators, excelImputationDTOS, imputation);

            getImputationsOfConnectedDelcoTeamOnly(imputation);
            imputationConverterUtilService.sortImputations(imputation);

            excelFile.close();
            return Optional.of(imputation);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Map<String, Integer> getHeaderCoumns(Sheet sheet) {
        Map<String, Integer> headerColumns = new HashMap<>();
        Row row = sheet.getRow(HEADER_INDEX);
        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
            Cell cell = row.getCell(j);
            if(cell != null) headerColumns.put(cell.getStringCellValue(), j);
        }
        return headerColumns;
    }

    private void getImputationsOfConnectedDelcoTeamOnly(Imputation imputation) {
        if(SecurityUtils.isCurrentUserInRole(ROLE_DELCO)){
            Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get());
            Optional<Team> team = teamService.findOneByDelco(user.get().getId());
            imputation.setMonthlyImputations(getTeamMembersOnly(imputation.getMonthlyImputations(), team));
        }
    }

    private Set<CollaboratorMonthlyImputation> getTeamMembersOnly(Set<CollaboratorMonthlyImputation> monthlyImputations, Optional<Team> team) {
        return monthlyImputations.stream().filter(monthlyImputation ->
            monthlyImputation.getCollaborator().getTeam() != null
                && monthlyImputation.getCollaborator().getTeam().getId().equals(team.get().getId())
        ).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Optional<Sheet> getWeeklyActualEffortSheet(MultipartFile file, InputStream excelFile) throws IOException {
        String extension = FileExtensionUtil.getExtension(file.getOriginalFilename());
        return getRows(excelFile, extension);
    }

    private void createDailyImputationsForEachCollab(Set<String> ppmcIDs, List<CollabExcelImputationDTO> excelImputationDTOS, Imputation imputation) {
        Set<CollaboratorMonthlyImputation> monthlyImputations = new HashSet<>();
        ppmcIDs.forEach(ppmcId -> {
            Correspondence correspondence = correspondenceRepository.findByIdPPMC(ppmcId);
            if(isCorrespondenceNotExist(correspondence)){
                return;
            }else{
                Collaborator collaborator = correspondence.getCollaborator();
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

    private void setTotalOfMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation) {
        monthlyImputation.getDailyImputations().forEach(daily -> {
            monthlyImputation.setTotal(monthlyImputation.getTotal() + daily.getCharge());
        });
    }

    private void createDailyImputationFromExcelImputations(List<CollabExcelImputationDTO> excelImputationDTOS, String ppmcId, CollaboratorMonthlyImputation monthlyImputation, Set<CollaboratorDailyImputation> dailyImputations) {
        excelImputationDTOS.forEach(dto -> {
            if(dto.getCollaborator().equals(ppmcId)){
                if(isDailyImputationExist(dailyImputations, dto.getDay())){
                    setChargeToExistingDailyImputation(dailyImputations, dto.getDay(), dto.getCharge());
                }else{
                    CollaboratorDailyImputation dailyImputation = imputationConverterUtilService.createDailyImputation(dto.getDay(), dto.getCharge(), monthlyImputation);
                    dailyImputations.add(dailyImputation);
                }
            }
        });
    }

    private boolean isCorrespondenceNotExist(Correspondence correspondence) {
        return correspondence == null;
    }

    private boolean isDailyImputationExist(Set<CollaboratorDailyImputation> dailyImputations, int day) {
        return dailyImputations.stream().anyMatch(dailyImputation -> dailyImputation.getDay().equals(day));
    }

    private void setChargeToExistingDailyImputation(Set<CollaboratorDailyImputation> dailyImputations, int day, double charge) {
        double oldCharge = dailyImputations.stream().filter(
            daily -> daily.getDay().equals(day)
        ).findFirst().get().getCharge();
        dailyImputations.stream().filter(daily -> daily.getDay().equals(day)).findFirst().get().setCharge(oldCharge + charge);
    }

    private List<CollabExcelImputationDTO> ignoreOutOfOfficeImputations(List<CollabExcelImputationDTO> excelImputationDTOS) {
        return excelImputationDTOS.stream().filter(
            collabExcelImputationDTO -> !collabExcelImputationDTO.getProjectRequestMisc().equals(OUT_OF_OFFICE)).collect(Collectors.toList()
        );
    }

    private Imputation createPpmcImputation(Sheet rows, Map<String, Integer> headerColumns) {
        ImputationType imputationType = imputationConverterUtilService.findImputationTypeByNameLike(PPMC_TYPE_NAME);
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

    private void setCharge(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer manDayColumnIndex) {
        if(currentElementColumnIndex == manDayColumnIndex){
            dto.setCharge(cell.getNumericCellValue());
        }
    }

    private void setDay(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer dayColumnIndex) {
        if(currentElementColumnIndex == dayColumnIndex){
            dto.setDay((int) cell.getNumericCellValue());
        }
    }

    private void setProjectRequestMisc(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer requestMiscColumnIndex) {
        if(currentElementColumnIndex == requestMiscColumnIndex){
            dto.setProjectRequestMisc(cell.getStringCellValue());
        }
    }

    private void setCollaborator(CollabExcelImputationDTO dto, int currentElementColumnIndex, Cell cell, Integer idPpmcColumnIndex) {
        if (currentElementColumnIndex == idPpmcColumnIndex) {
            dto.setCollaborator(cell.getStringCellValue());
        }
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

    private Optional<Sheet> getRows(InputStream excelFile, String extension) throws IOException {
        if(FileExtensionUtil.isXLS(extension)){
            HSSFWorkbook workbook = new HSSFWorkbook(excelFile);
            return Optional.of(workbook.getSheet(WEEKLY_ACTUAL_EFFORT_COLUMN));
        }
        if(FileExtensionUtil.isXLSX(extension)){
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            return Optional.of(workbook.getSheet(WEEKLY_ACTUAL_EFFORT_COLUMN));
        }
        return Optional.empty();
    }
}
