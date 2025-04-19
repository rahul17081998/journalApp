package com.rahul.journal_app.constants;

public enum ErrorCode {
    USER_NOT_VERIFIED("ERR_USER_NOT_VERIFIED", "User is not verified"),
    INCORRECT_PASSWORD("ERR_INCORRECT_PASSWORD", "Incorrect password provided"),
    FAILED_TO_CREATE_JWT_TOKEN("ERR_FAILED_TO_CREATE_JWT_TOKEN", "Failed to create jwt token"),
    FAILED_TO_GENERATE_LOGIN_RESPONSE("ERR_LOGIN_RESPONSE", "Failed to generate login response"),
    PROFILE_PHOTO_UPDATE_FAILED("ERR_USER_DETAILS_NOT_UPDATED", "Failed to update profile photo"),
    USER_NOT_FOUND("ERR_USER_NOT_FOUND", "User not found"),
    INVALID_PHONE_NUMBER("ERR_INVALID_PHONE", "Invalid phone number"),
    INVALID_DATE_OF_BIRTH("ERR_INVALID_DOB", "Invalid date of birth"),
    INVALID_GENDER("ERR_INVALID_GENDER", "Invalid gender"),
    CITY_FIELD_EMPTY_OR_NULL("ERR_INVALID_CITY","The city field for the user is either empty or null."),
    WEATHER_REPORT_FETCH_FAILED("ERR_WEATHER_REPORT_FETCH_FAILED", "Failed to fetch weather report for the provided city"),
    INVALID_EMAIL_FORMAT("ERR_INVALID_EMAIL_FORMAT", "Invalid email format"),
    USER_ALREADY_EXIST("ERR_USER_ALREADY_EXIST", "User already exist"),
    PASSWORD_CAN_NOT_BE_NULL_OR_EMPTY("ERR_PASSWORD_CAN_NOT_BE_NULL_OR_EMPTY", "Password can't be null or empty"),
    USER_REGISTRATION_FAILED("ERR_USER_REGISTRATION_FAILED", "Failed to register user"),
    EMAIL_SENDING_FAILED("ERR_EMAIL_SENDING_FAILED", "Failed to send email."),
    EMAIL_NOT_FOUND("ERR_EMAIL_NOT_FOUND", "Email not registered"),
    PASSWORD_RESET_FAILED("ERR_PASSWORD_RESET_FAILED", "Failed to reset the password"),
    OTP_MISSING_FROM_LINK("ERR_OTP_MISSING_FROM_LINK", "The verification link is missing the OTP. Please try again."),
    OTP_NOT_FOUND_IN_DB("ERR_OTP_NOT_FOUND_IN_DB", "No OTP found for this request. It may have already been used or never generated."),
    OTP_INVALID("ERR_OTP_INVALID", "The verification link is invalid."),
    OTP_EXPIRED("ERR_OTP_EXPIRED", "This verification link has expired. Please request a new one."),
    OTP_ALREADY_USED("ERR_OTP_ALREADY_USED", "This verification link has already been used."),
    USER_ALREADY_VERIFIED("ERR_USER_ALREADY_VERIFIED", "Your email is already verified."),
    FAILED_TO_VERIFY_USER_EMAIL("ERR_USER_VERIFICATION_FAILED", "Something went wrong while verifying your email."),
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
