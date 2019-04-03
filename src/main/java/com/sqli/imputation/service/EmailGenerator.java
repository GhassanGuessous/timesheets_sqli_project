package com.sqli.imputation.service;

public interface EmailGenerator {

    String generate(String firstname, String lastname, boolean isEmailDuplicated);
}
