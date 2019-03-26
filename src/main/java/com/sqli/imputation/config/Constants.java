package com.sqli.imputation.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC_AUTH = "Basic Kraouine/*TBP*/Ironm@n2019";
    public static final String TBP_URL_WEB_SERVICE = "http://tbp-maroc.sqli.com/restService/public/";
    public static final String JSON_RESULT_FORMAT = ".json";
    
    private Constants() {
    }
}
