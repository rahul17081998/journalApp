package com.rahul.journal_app.constants;

public enum ErrorCode {

    PROFILE_PHOTO_UPDATE_FAILED("ERR_USER_DETAILS_NOT_UPDATED", "Failed to update profile photo"),
    USER_NOT_FOUND("ERR_USER_NOT_FOUND", "User not found"),
    INVALID_PHONE_NUMBER("ERR_INVALID_PHONE", "Invalid phone number"),
    INVALID_DATE_OF_BIRTH("ERR_INVALID_DOB", "Invalid date of birth"),
    INVALID_GENDER("ERR_INVALID_GENDER", "Invalid gender"),
    CITY_FIELD_EMPTY_OR_NULL("ERR_INVALID_CITY","The city field for the user is either empty or null."),
    WEATHER_REPORT_FETCH_FAILED("ERR_WEATHER_REPORT_FETCH_FAILED", "Failed to fetch weather report for the provided city")

    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
