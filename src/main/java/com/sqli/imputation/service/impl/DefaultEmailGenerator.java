package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.EmailGenerator;
import org.springframework.stereotype.Service;

@Service
public class DefaultEmailGenerator implements EmailGenerator {

    public static final String SQLI_COM = "@sqli.com";
    public static final String SQLI_DEFAULT_EMAIL = "sqli";
    public static final String SPACE = " ";

    @Override
    public String generate(String firstname, String lastname, boolean isEmailDuplicated) {
        StringBuilder email = new StringBuilder();
        if (isEmailDuplicated)
            email.append(getFirstTwoLetters(firstname.toLowerCase()));
        else
            email.append(getEmailPrefix(firstname.toLowerCase()));
        email.append(lastname.toLowerCase().replaceAll(SPACE, ""));
        email.append(SQLI_COM);
        return email.toString();
    }

    private String getFirstLetter(String firstname) {
        return String.valueOf(firstname.charAt(0));
    }

    private String getFirstTwoLetters(String firstname) {
        if (firstname.isEmpty())
            return SQLI_DEFAULT_EMAIL;
        return firstname.substring(0, 2);
    }

    private String getEmailPrefix(String firstname) {
        if (firstname.isEmpty())
            return SQLI_DEFAULT_EMAIL;
        else {
            if (isComposed(firstname)) {
                String[] tokens = firstname.split(SPACE);
                return getFirstLetterOfEach(tokens);
            }
            return getFirstLetter(firstname);
        }
    }

    private String getFirstLetterOfEach(String[] tokens) {
        StringBuilder emailPrefix = new StringBuilder();
        for (String token : tokens) {
            emailPrefix.append(getFirstLetter(token));
        }
        return emailPrefix.toString();
    }

    private boolean isComposed(String firstname) {
        return firstname.contains(SPACE);
    }
}
