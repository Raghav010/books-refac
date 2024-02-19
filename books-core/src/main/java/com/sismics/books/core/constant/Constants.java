package com.sismics.books.core.constant;


/**
 * Application constants.
 * 
 * @author jtremeaux
 */
public class Constants {
    /**
     * Default locale.
     */
    public static final String DEFAULT_LOCALE_ID = "en";

    /**
     * Default timezone ID.
     */
    public static final String DEFAULT_TIMEZONE_ID = "Europe/London";
    
    /**
     * Default theme ID.
     */
    public static final String DEFAULT_THEME_ID = "default.less";
    
    
    /**
     * Default generic user role.
     */
    public static final String DEFAULT_USER_ROLE = "user";

    public static final int MIN_USERNAME_LEN = 3;
    public static final int MAX_USERNAME_LEN = 50;

    public static final int MIN_EMAIL_LEN = 3;
    public static final int MAX_EMAIL_LEN = 50;
    public static final boolean EMAIL_NULLABLE = true;

    public static final int MIN_PWD_LEN = 8;
    public static final int MAX_PWD_LEN = 50;
    public static final boolean PWD_NULLABLE = true;

    public static final boolean LOCALE_ID_NULLABLE = true;

    public static final boolean THEME_ID_NULLABLE = true;
}
