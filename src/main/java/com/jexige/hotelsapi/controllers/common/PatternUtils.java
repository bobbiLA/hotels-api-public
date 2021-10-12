package com.jexige.hotelsapi.controllers.common;

public class PatternUtils {
    public static final String ALLOWED_PHONE_PATTERN = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
    public static final String PHONE_MISMATCH_MESSAGE = "must be valid (only characters allowed are: plus at the beginning, numbers, dots, hyphens and blank spaces)";

    public static final String ALLOWED_EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String EMAIL_MISMATCH_MESSAGE = "must be valid";
}
