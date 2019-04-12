package com.sqli.imputation.service.util;

public final class FileExtensionUtil {

    public static final String XLS_EXTENSION = "xls";
    public static final String XLSX_EXTENSION = "xlsx";
    public static final int EXTENSION_POSITION = 1;


    public static boolean isNotValidExcelExtension(String extension){
        return !isXLS(extension) && !isXLSX(extension);
    }

    public static boolean isXLSX(String extension) {
        return extension.equals(XLSX_EXTENSION);
    }

    public static boolean isXLS(String extension) {
        return extension.equals(XLS_EXTENSION);
    }

    public static String getExtension(String originalFilename) {
        return originalFilename.split("\\.")[EXTENSION_POSITION];
    }
}
