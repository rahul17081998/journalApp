package com.rahul.journal_app.utils;

import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class Util {

    @Value("${url.baseUrl}")
    private String baseUrl;

    @Value("${otp.expiration_time}")
    private Long otpExpiredTimeInMinute;

    @Autowired
    private UserRepository userRepository;

    public String capitalizeFirstChar(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return as is for null or empty input
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Transactional
    public boolean isValidEmail(String email){
        if(email!=null && email.contains("@")){
            int atIndex=email.indexOf("@");
            int dotIndex=email.indexOf(".", atIndex);
            return atIndex>0 && dotIndex>atIndex+1 && dotIndex<email.length()-1;
        }
        return false;
    }

    public String getBodyForResetPasswordSendOtpMail(String firstName, String userName, String otp) {
        Long otpExpTime = otpExpiredTimeInMinute;
        String body = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "  .email-container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9; }" +
                "  .otp { font-size: 20px; font-weight: bold; color: #007BFF; }" +
                "  .footer { font-size: 12px; color: #999; text-align: center; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "  <div class='email-container'>" +
                "    <p>Dear <strong>" + capitalizeFirstChar(firstName) + "</strong>,</p>" +
                "    <p>We’ve received a request to reset the password for your account.</p>" +
                "    <p>To proceed, please use the following OTP (One-Time Password) to reset your password:</p>" +
                "    <p class='otp'>" + otp + "</p>" +
                "    <p><strong>Note:</strong> This OTP will expire in " + otpExpTime + " minutes. Please use it before it expires.</p>" +
                "    <p>If you did not request this password reset, please ignore this email or contact our support team.</p>" +
                "    <p>Best regards,</p>" +
                "    <p>The JournalApp Team</p>" +
                "    <hr>" +
                "    <p class='footer'>This is a system-generated email. Please do not reply to this message.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";
        return body;
    }

    public boolean isValidPhoneNumber(String phoneNo) {
        if(phoneNo.length()!=10) return false;
        return phoneNo.chars().allMatch(Character::isDigit); // Ensure all characters are digits
    }

    public boolean isValidDateOfBirth(String dateOfBirth, String format) {
        if (dateOfBirth == null || format == null) {
            return false; // Invalid if either date or format is null
        }
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern(format);
        } catch (IllegalArgumentException e) {
            return false; // Invalid format pattern
        }

        try {

            LocalDate.parse(dateOfBirth, formatter);
            return true; // Date is valid
        } catch (DateTimeParseException e) {
            return false; // Date is invalid
        }
    }

    public boolean isAdmin(String userName){
        User user = userRepository.findByUserName(userName);
        boolean hasUserAdminRole = false;
        if(user!=null){
            List<String> roles = user.getRoles();
            for(String role: roles){
                if(role.toUpperCase().equals("ADMIN")){
                    hasUserAdminRole=true;
                    break;
                }
            }

        }
        return hasUserAdminRole;
    }

    public String getBodyForOtpVerificationMail(String firstName, String userName, String otp) {
        firstName=(firstName==null || firstName.equals(""))? "User":firstName;
        // String verifyUserEmailUrl= baseUrl + "/journal/public/verify-user?"+"userName="+userName+"&otp="+otp;
        Long linkExpTime=otpExpiredTimeInMinute;
        String verifyUserEmailUrl=UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/journal/public/verify-user")
                .queryParam("userName", userName)
                .queryParam("otp", otp)
                .toUriString();

        String body = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "  .email-container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9; }" +
                "  .button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff !important; text-decoration: none; background-color: #007BFF; border-radius: 5px; margin-top: 20px; text-align: center; }" +
                "  .button:hover { background-color: #0056b3; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "  <div class='email-container'>" +
                "    <p>Dear <strong>" + capitalizeFirstChar(firstName) + "</strong>,</p>" +
                "    <p>Thank you for signing up for JournalApp! To complete your registration and verify your email address, please click the button below:</p>" +
                "    <a href='" + verifyUserEmailUrl + "' class='button'>Verify Email</a>" +
                "    <p><strong>Note:</strong> This verification link will expire in "+ linkExpTime +" minutes. Please verify your email promptly.</p>" +
                "    <p>Best regards,</p>" +
                "    <p>The JournalApp Team</p>" +
                "    <hr>" +
                "    <p style='font-size: 12px; color: #999;'>This is a system-generated email. Please do not reply to this message.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";
        return body;
    }
}
