package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.CollaboratorRepository;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.repository.ImputationTypeRepository;
import com.sqli.imputation.service.PpmcImputationConverterService;
import com.sqli.imputation.service.dto.CollabExcelImputationDTO;
import com.sqli.imputation.service.factory.ImputationFactory;
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

    public static final int SECOND_SHEET = 1;
    public static final String PPMC_TYPE_NAME = "ppmc";
    public static final int PROJECT_REQUEST_MISC_COLUMN = 11;
    public static final int NPDI_PROJECT_COLUMN = 13;
    public static final int DAY_COLUMN = 17;
    public static final int MAN_DAY = 20;
    public static final String OUT_OF_OFFICE = "Out of Office";
    public static final int ID_PPMC_COLUMN = 1;

    @Autowired
    private ImputationFactory imputationFactory;
    @Autowired
    private ImputationTypeRepository imputationTypeRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;

    @Override
    public Imputation getPpmcImputationFromExcelFile(MultipartFile file) {
        try {
            InputStream excelFile = file.getInputStream();
            String extension = FileExtensionUtil.getExtension(file.getOriginalFilename());
            Sheet sheet = getRows(excelFile, extension);

            Imputation imputation = createPpmcImputation();
            Set<String> collaborators = getCollaborators(sheet);

            List<CollabExcelImputationDTO> excelImputationDTOS = getExcelCollabDTOS(sheet);
            excelImputationDTOS = ignoreOutOfOfficeImputations(excelImputationDTOS);

            createDailyImputationsForEachCollab(collaborators, excelImputationDTOS, imputation);

            excelFile.close();
            return imputation;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createDailyImputationsForEachCollab(Set<String> ppmcIDs, List<CollabExcelImputationDTO> excelImputationDTOS, Imputation imputation) {
        Set<CollaboratorMonthlyImputation> monthlyImputations = new HashSet<>();

        ppmcIDs.forEach(ppmcId -> {
            Correspondence correspondence = correspondenceRepository.findByIdPPMC(ppmcId);
            if(isCorrespondenceNotExist(correspondence)){
                return;
            }else{
                Collaborator collaborator = correspondence.getCollaborator();
                CollaboratorMonthlyImputation monthlyImputation = imputationFactory.createMonthlyImputation(imputation, collaborator);
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
                    CollaboratorDailyImputation dailyImputation = imputationFactory.createDailyImputation(dto.getDay(), dto.getCharge(), monthlyImputation);
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
        double oldCharge = dailyImputations.stream().filter(daily -> daily.getDay().equals(day)).findFirst().get().getCharge();
        dailyImputations.stream().filter(daily -> daily.getDay().equals(day)).findFirst().get().setCharge(oldCharge + charge);
    }

    private List<CollabExcelImputationDTO> ignoreOutOfOfficeImputations(List<CollabExcelImputationDTO> excelImputationDTOS) {
        return excelImputationDTOS.stream().filter(collabExcelImputationDTO -> !collabExcelImputationDTO.getProjectRequestMisc().equals(OUT_OF_OFFICE)).collect(Collectors.toList());
    }

    private Imputation createPpmcImputation() {
        ImputationType imputationType = imputationTypeRepository.findByNameLike(PPMC_TYPE_NAME);
        return imputationFactory.createImputation(2019, 02, imputationType);
    }

    private List<CollabExcelImputationDTO> getExcelCollabDTOS(Sheet sheet) {
        List<CollabExcelImputationDTO> excelImputationDTOS = new ArrayList<>();
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            CollabExcelImputationDTO dto = new CollabExcelImputationDTO();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                buildCollabExcelDTO(dto, j, cell);
            }
            excelImputationDTOS.add(dto);
        }
        return excelImputationDTOS;
    }

    private void buildCollabExcelDTO(CollabExcelImputationDTO dto, int j, Cell cell) {
        setCollaborator(dto, j, cell);
        setProjectRequestMisc(dto, j, cell);
        setNPDIProject(dto, j, cell);
        setDay(dto, j, cell);
        setCharge(dto, j, cell);
    }

    private void setCharge(CollabExcelImputationDTO dto, int j, Cell cell) {
        if(j == MAN_DAY){
            dto.setCharge(cell.getNumericCellValue());
        }
    }

    private void setDay(CollabExcelImputationDTO dto, int j, Cell cell) {
        if(j == DAY_COLUMN){
            dto.setDay((int) cell.getNumericCellValue());
        }
    }

    private void setNPDIProject(CollabExcelImputationDTO dto, int j, Cell cell) {
        if(j == NPDI_PROJECT_COLUMN){
            dto.setNpdiProject(cell.getStringCellValue());
        }
    }

    private void setProjectRequestMisc(CollabExcelImputationDTO dto, int j, Cell cell) {
        if(j == PROJECT_REQUEST_MISC_COLUMN){
            dto.setProjectRequestMisc(cell.getStringCellValue());
        }
    }

    private void setCollaborator(CollabExcelImputationDTO dto, int j, Cell cell) {
        if (j == ID_PPMC_COLUMN) {
            dto.setCollaborator(cell.getStringCellValue());
        }
    }

    private Set<String> getCollaborators(Sheet sheet) {
        Set<String> collaborators = new HashSet<>();
        //I'm ignoring header for that I have +1 in loop
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (j == ID_PPMC_COLUMN) {
                    collaborators.add(cell.getStringCellValue());
                }
            }
        }
        return collaborators;
    }

    private Sheet getRows(InputStream excelFile, String extension) throws IOException {
        Sheet sheet = null;
        if(FileExtensionUtil.isXLS(extension)){
            HSSFWorkbook workbook = new HSSFWorkbook(excelFile);
            sheet = workbook.getSheetAt(SECOND_SHEET);
        }
        if(FileExtensionUtil.isXLSX(extension)){
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            sheet = workbook.getSheetAt(SECOND_SHEET);
        }
        return sheet;
    }
}
