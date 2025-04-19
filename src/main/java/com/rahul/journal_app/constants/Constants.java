package com.rahul.journal_app.constants;

public interface Constants {

    String WEATHER_API_KEY="weatherApiKey";
    String CITY="city";
    String WEATHER_API_URL="weatherApiUrl";
    public static String INVALID_EMAIL_FORMAT="Invalid email format";
    public static String USER_REGISTRATION_SUCCESSFUL = "Registration successful! Please verify your email to complete the process.";
    public static String INCORRECT_USERNAME_OR_PASSWORD="Incorrect username or password";
    public static String EMAIL_NOT_FOUND ="Email not found";
    public static String USER_VERIFICATION_SUCCESSFUL ="Your email is verified successfully";
    public static final String EMAIL_SENT_SUCCESSFULLY_MSG = "Email sent successfully";
    public static final String USER_NOT_VERIFIED = "User is not verified";
    public static final String USER_ALREADY_EXIST = "User already exist";
    public static final String USER_DETAILS_UPDATED_SUCCESSFULLY = "User details updated successfully";
    public static final String ADMIN_ACCESS_GRANT_EXCEPTION = "Exception while granting admin access of a user";
    public static final String ADMIN_ACCESS_REMOVE_EXCEPTION = "Exception while removing admin access of a user";
    public static final String USER_DOES_NOT_HAVE_ADMIN_ACCESS = "User does not have admin access.";
    public static final String USER_VERIFICATION_EMAIL_SENT_SUCCESSFULLY = "A verification email has been sent to the user successfully.";
    public static final String SIGNUP_SUCCESSFUL_MSG = "Signup successful";
    public static final String PASSWORD_CAN_NOT_BE_NULL_OR_EMPTY="Password can't be null or empty";
    public static final String EMPTY_FILE = "Cannot upload empty file";





    /*-----------------------------------------Exceptions--------------------------------------------*/

    public static final String EXCEPTION_OCCURRED_DURING_USER_VERIFICATION="Exception occurred during user verification";

    public static final String PASSWORD_RESET_EXCEPTION_OCCURRED = "Exception occurred during password reset";
    public static final String INVALID_OTP_EXCEPTION = "Invalid otp exception";
    public static final String OTP_EXPIRED = "Otp expired";
    public static final String OTP_NULL_OR_EMPTY_EXCEPTION = "Otp null or empty exception";
    public static final String PASSWORD_RESET_SUCCESSFUL = "Password reset successful";
    public static final String EXCEPTION_OCCURRED_DURING_USER_REGISTRATION = "Exception occurred during registration";
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    public static final String ADMIN_ACCESS_GRANTED = "Admin access provided to the user";
    public static final String USER_ALREADY_HAS_ADMIN_ACCESS = "User has already admin access";
    public static final String INVALID_LINK = "The link you provided is invalid. Please check the link or request a new verification email.";
    public static final String LINK_EXPIRED = "The verification link has expired. Please request a new verification link to complete your email verification.";
    public static final String USER_ALREADY_VERIFIED = "Your email is already verified";

    // Success messages
    public static final String USER_PROFILE_PICTURE_UPDATED_SUCCESSFULLY_MSG = "User profile picture updated successfully";
    public static final String USER_DETAILS_FETCHED_SUCCESSFULLY_MSG = "User details fetched successfully";
    public static final String WEATHER_FETCH_SUCCESSFULLY_MSG = "Weather fetch successfully";
    public static final String LOGIN_SUCCESSFUL_MSG = "Login successful";



    // Error messages
    public static final String USER_NOT_FOUND_MSG = "User not found with the provided username.";
    public static final String USER_UPDATE_FAILED_MSG = "Failed to update user information.";
    public static final String PROFILE_PHOTO_UPDATE_FAILED_MSG = "Failed to update profile photo.";
    public static final String FAILED_TO_FETCH_USER_DETAILS_MSG = "Failed to fetch user details";

    public static final String INVALID_PHONE_NUMBER_MSG = "Invalid Phone number";
    public static final String INVALID_DATE_OF_BIRTH_MSG = "Invalid date of birth";
    public static final String INVALID_GENDER_MSG = "Invalid gender provided. it should be either male or female";



    // Error codes
    public static final String USER_NOT_FOUND = "ERR_USER_NOT_FOUND";
    public static final String USER_DETAILS_NOT_UPDATED = "ERR_USER_DETAILS_NOT_UPDATED";
    public static final String USER_NOT_UPDATED = "ERR_USER_NOT_UPDATED";
    public static final String INVALID_PHONE_NUMBER = "ERR_INVALID_PHONE";



}
