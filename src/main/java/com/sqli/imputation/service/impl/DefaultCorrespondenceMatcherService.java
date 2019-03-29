package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.service.CorrespondenceMatcherService;
import com.sqli.imputation.service.dto.CorrespondenceDTO;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DefaultCorrespondenceMatcherService implements CorrespondenceMatcherService {

    private static final int SEVENTH_SHEET = 6;
    private static final int ELEVENTH_COLUMN = 11;
    private static final int TWELFTH_COLUMN = 12;
    private static final String AT_SYMBOL = "@";
    private static final int FIRST_POSITION = 0;

    @Autowired
    private CorrespondenceRepository correspondenceRepository;

    @Override
    public List<CorrespondenceDTO> getCorrespondencesFromExcelFile(String fileName) {
        try {
            FileInputStream file = new FileInputStream(new File(fileName));
            //Create Workbook instance holding reference to .xls file
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            //Get seventh/desired sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(SEVENTH_SHEET);

            ArrayList<CorrespondenceDTO> correspondenceDTOS = new ArrayList<>();
            //I've Header and I'm ignoring header for that I've +1 in loop
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                CorrespondenceDTO correspondenceDTO = new CorrespondenceDTO();
                Row row = sheet.getRow(i);

                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (j == ELEVENTH_COLUMN) {
                        correspondenceDTO.setId_ppmc(cell.getStringCellValue());
                    }
                    if (j == TWELFTH_COLUMN) {
                        correspondenceDTO.setId_app(cell.getStringCellValue());
                    }
                }
                correspondenceDTOS.add(correspondenceDTO);
            }
            file.close();
            return correspondenceDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void match(List<Correspondence> correspondences, String fileName) {
        List<CorrespondenceDTO> correspondenceDTOS = getCorrespondencesFromExcelFile(fileName);
        correspondences.forEach(correspondence -> {
            Collaborator collaborator = correspondence.getCollaborator();
            correspondenceDTOS.forEach(dto -> {
                if(dto.getId_app() != null && isAppIdsAreEqual(collaborator, dto)){
                    setAPP_PPMC_Ids(correspondence, dto);
                }
            });
        });
    }

    private void setAPP_PPMC_Ids(Correspondence correspondence, CorrespondenceDTO dto) {
        correspondence.setIdAPP(dto.getId_app());
        correspondence.setIdPPMC(dto.getId_ppmc());
        correspondenceRepository.save(correspondence);
    }

    private boolean isAppIdsAreEqual(Collaborator collaborator, CorrespondenceDTO dto) {
        return dto.getId_app().equals(getAPPIdFromEmail(collaborator.getEmail()));
    }

    private String getAPPIdFromEmail(String email) {
        return email.split(AT_SYMBOL)[FIRST_POSITION];
    }


}
