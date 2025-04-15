package com.rahul.journal_app.constants;

public interface Constants {

    String WEATHER_API_KEY="weatherApiKey";
    String CITY="city";
    String WEATHER_API_URL="weatherApiUrl";
    public static String INVALID_EMAIL_FORMAT="Invalid email format";
    public static String USER_REGISTRATION_SUCCESSFUL = "Registration successful! Please verify your email to complete the process.";
    public static String INCORRECT_USERNAME_OR_PASSWORD="Incorrect username or password";
    public static String EMAIL_NOT_FOUND ="Email not found";
    public static String USER_VERIFICATION_SUCCESSFUL ="User verification successful";
    public static String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_SUCCESSFULLY_SENT = "Email successfully sent";
    public static final String USER_NOT_VERIFIED = "User is not verified";
    public static final String USER_ALREADY_EXIST = "User already exist";
    public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully";
    public static final String USER_NOT_UPDATED = "User not updated";
    public static final String ADMIN_ACCESS_GRANT_EXCEPTION = "Exception while granting admin access of a user";
    public static final String ADMIN_ACCESS_REMOVE_EXCEPTION = "Exception while removing admin access of a user";
    public static final String USER_DOES_NOT_HAVE_ADMIN_ACCESS = "User does not have admin access.";
    public static final String USER_VERIFICATION_EMAIL_SENT_SUCCESSFULLY = "A verification email has been sent to: ";
    public static final String PASSWORD_CAN_NOT_BE_NULL_OR_EMPTY="Password can't be null or empty";





    /*-----------------------------------------Exceptions--------------------------------------------*/

    public static final String EXCEPTION_OCCURRED_DURING_USER_VERIFICATION="Exception occurred during user verification";

    public static final String PASSWORD_RESET_EXCEPTION_OCCURRED = "Exception occurred during password reset";
    public static final String INVALID_OTP_EXCEPTION = "Invalid otp exception";
    public static final String OTP_EXPIRED = "Otp expired";
    public static final String OTP_NULL_OR_EMPTY_EXCEPTION = "Otp null or empty exception";
    public static final String PASSWORD_RESET_SUCCESSFUL = "Password reset successful";
    public static final String EXCEPTION_OCCURRED_DURING_USER_REGISTRATION = "Exception occurred during registration";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number provided";
    public static final String INVALID_DATE_OF_BIRTH = "Invalid D.O.B provided";
    public static final String INVALID_GENDER = "Invalid gender provided";
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    public static final String ADMIN_ACCESS_GRANTED = "Admin access provided to the user";
    public static final String USER_ALREADY_HAS_ADMIN_ACCESS = "User has already admin access";
    public static final String INVALID_LINK = "The link you provided is invalid. Please check the link or request a new verification email.";
    public static final String LINK_EXPIRED = "The verification link has expired. Please request a new verification link to complete your email verification.";
    public static final String USER_ALREADY_VERIFIED = "User already verified";
}
