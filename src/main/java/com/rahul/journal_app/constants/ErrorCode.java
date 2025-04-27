package com.rahul.journal_app.constants;

public enum ErrorCode {
    USER_NOT_VERIFIED("ERR_USER_NOT_VERIFIED", "User is not verified"),
    INCORRECT_PASSWORD("ERR_INCORRECT_PASSWORD", "Incorrect password provided"),
    FAILED_TO_CREATE_JWT_TOKEN("ERR_FAILED_TO_CREATE_JWT_TOKEN", "Failed to create jwt token"),
    FAILED_TO_GENERATE_LOGIN_RESPONSE("ERR_LOGIN_RESPONSE", "Failed to generate login response"),
    PROFILE_PHOTO_UPDATE_FAILED("ERR_USER_DETAILS_NOT_UPDATED", "Failed to update profile photo"),
    USER_NOT_FOUND("ERR_USER_NOT_FOUND", "User not found"),
    USER_ALREADY_HAS_ADMIN_ACCESS("ERR_USER_ALREADY_HAS_ADMIN_ACCESS", "User already has admin access."),
    USER_ALREADY_HAS_USER_ACCESS("ERR_USER_ALREADY_HAS_USER_ACCESS", "User already has USER access."),
    INVALID_PHONE_NUMBER("ERR_INVALID_PHONE_NUMBER", "Invalid phone number provided"),
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
    OTP_MISSING("ERR_OTP_MISSING", "OTP is missing. Please provide it."),

    OTP_MISSING_FROM_LINK("ERR_OTP_MISSING_FROM_LINK", "The verification link is missing the OTP. Please try again."),
    OTP_NOT_FOUND_IN_DB("ERR_OTP_NOT_FOUND_IN_DB", "No OTP found for this request. It may have already been used or never generated."),
    OTP_INVALID("ERR_OTP_INVALID", "The verification link is invalid."),
    INVALID_OTP_ERROR("ERR_INVALID_OTP", "Incorrect OTP provided."),
    OTP_EXPIRED("ERR_OTP_EXPIRED", "This verification link has expired. Please request a new one."),
    OTP_EXPIRED_ERROR("ERR_OTP_EXPIRED", "This otp has expired. Please request a new one."),
    OTP_ALREADY_USED("ERR_OTP_ALREADY_USED", "This verification link has already been used."),
    OTP_ALREADY_USED_ERROR("ERR_OTP_ALREADY_USED_ERROR", "This otp has already been used. Please request new otp"),
    USER_ALREADY_VERIFIED("ERR_USER_ALREADY_VERIFIED", "Your email is already verified."),
    FAILED_TO_VERIFY_USER_EMAIL("ERR_USER_VERIFICATION_FAILED", "Something went wrong while verifying your email."),
    FAILED_TO_RESET_PASSWORD("ERR_FAILED_TO_RESET_PASSWORD", "Something went wrong while updating password."),
    FAILED_TO_DELETE_USER_ACCOUNT("ERR_FAILED_TO_DELETE_USER_ACCOUNT", "Failed to delete user account"),
    FAILED_TO_UPDATE_ROLE("ERR_FAILED_TO_UPDATE_ROLE", "Failed to update role of user."),
    FAILED_TO_FETCH_USER_JOURNAL_ENTRIES("ERR_FAILED_TO_FETCH_USER_JOURNAL_ENTRIES", "Failed to fetch user journal entries"),
    FAILED_TO_CREATE_AND_SAVE_JOURNAL_ENTRY("ERR_FAILED_TO_CREATE_AND_SAVE_JOURNAL_ENTRY", "Failed to create and save journal entry"),
    FAILED_TO_UPDATE_JOURNAL_ENTRY("ERR_FAILED_TO_UPDATE_JOURNAL_ENTRY", "Failed to update journal entry"),
    JOURNAL_OBJECT_ID_MISSING("ERR_JOURNAL_OBJECT_ID_MISSING", "journal entry Object Id is missing"),
    JOURNAL_ENTRY_NOT_FOUND_IN_DB("ERR_JOURNAL_ENTRY_NOT_FOUND_IN_DB", "journal entry not found in db"),
    JOURNAL_UPDATE_UNAUTHORIZED("ERR_JOURNAL_UPDATE_UNAUTHORIZED", "User is not allowed to update this journal"),


    TITLE_EMPTY_ERROR("ERR_TITLE_EMPTY", "Journal title is required and cannot be left blank."),
    CONTENT_EMPTY_ERROR("ERR_CONTENT_EMPTY", "Please provide some content for the journal entry."),
    INVALID_SENTIMENT_ERROR("ERR_INVALID_SENTIMENT", "Invalid sentiment value. Accepted values are: HAPPY, SAD, ANGRY, or ANXIOUS."),
    INVALID_PRIVACY_LEVEL_ERROR("ERR_INVALID_PRIVACY_LEVEL_ERROR", "Invalid privacy level value. Accepted values are: PRIVATE, SHARED or PUBLIC."),
    INVALID_JOURNAL_CATEGORY_ERROR("ERR_INVALID_JOURNAL_CATEGORY_ERROR", "Invalid category value. Accepted values are: DAILY, GRATITUDE, REFLECTION, GOALS, DREAM, TRAVEL, HEALTH, WORK or OTHER"),
    INVALID_JOURNAL_SORT_BY_VALUE_ERROR("ERR_INVALID_JOURNAL_SORT_BY_VALUE_ERROR", "Invalid sortBy value. Accepted values are: LIKE, COMMENT or VIEW "),
    INVALID_EMOTION_RATING_ERROR("ERR_INVALID_EMOTION_RATING_ERROR", "Invalid emotion rating value. Accepted values would be in between 1-10"),
    INVALID_JOURNAL_ID_ERROR("ERR_INVALID_JOURNAL_ID_ERROR", "Invalid journal ID provided"),





    EXCEPTION_WHILE_DELETING_USER_ACCOUNT("FOUND_EXCEPTION_TO_WHILE_DELETING_USER_AC", "Something went wrong while deleting user your email."),
    EXCEPTION_WHILE_UPDATING_USER_ROLE("EXCEPTION_WHILE_UPDATING_USER_ROLE", "Something went wrong while updating role of user."),
    EXCEPTION_WHILE_ADDING_JOURNAL_ENTRY("EXCEPTION_WHILE_ADDING_JOURNAL_ENTRY", "Something went wrong while adding journal entry."),
    EXCEPTION_WHILE_UPDATING_JOURNAL_ENTRY("EXCEPTION_EXCEPTION_WHILE_UPDATING_JOURNAL_ENTRY", "Something went wrong while updating journal entry."),
    EXCEPTION_WHILE_FETCHING_JOURNAL_ENTRY("EXCEPTION_OCCURRED_WHILE_FETCHING_JOURNAL_ENTRY", "Something went wrong while fetching journal entry."),
    EXCEPTION_WHILE_SHARING_JOURNAL_ENTRY("EXCEPTION_OCCURRED_WHILE_SHARING_JOURNAL_ENTRY", "Something went wrong while sharing journal entry."),
    FAILED_TO_SEND_MSG("ERR_FAILED_TO_SEND_MSG", "Exception while sending sms to user."),
    ACCESS_DENIED("ERR_ACCESS_DENIED","You don't have permission to Access the resource" ),
    ACCESS_DENIED_TO_SHARE("ERR_ACCESS_DENIED_TO_SHARE","You don't have permission to share the resource" ),
    SENDER_AND_RECEIVER_ARE_SAME("ERR_SENDER_AND_RECEIVER_ARE_SAME","Sender and receiver are same" );

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
