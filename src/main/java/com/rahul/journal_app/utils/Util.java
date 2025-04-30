package com.rahul.journal_app.utils;

import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.enums.*;
import com.rahul.journal_app.repository.AttachmentRepository;
import com.rahul.journal_app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
@Slf4j
@Component
public class Util {

    @Value("${url.baseUrl}")
    private String baseUrl;

    @Value("${otp.expiration_time}")
    private Long otpExpiredTimeInMinute;

    @Autowired
    private AttachmentRepository attachmentRepository;

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

        /*
        // Create a visually appealing text-based logo for fallback
        String textLogoHtml = "<div style=\"font-size: 36px; font-weight: bold; background-color: #0066a1; color: white; " +
                              "padding: 15px; border-radius: 10px; text-align: center; margin: 0 auto; max-width: 200px;\">" +
                              "VELINQ</div>";
        */

        // Use CID reference for logo - this will be replaced by EmailService
        String logoHtml = "<img src=\"cid:logo\" alt=\"VELINQ Logo\" width=\"100\" height=\"auto\" " +
                          "style=\"display: block; margin: 0 auto; max-width: 100px; border: 0; outline: none;\" />";

        String body = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />" +
                "<title>Reset Password</title>" +
                "<style type=\"text/css\">" +
                "body { margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333333; }" +
                ".container { max-width: 600px; }" +
                ".otp { font-size: 24px; font-weight: bold; color: #007BFF; text-align: center; padding: 10px; background-color: #f0f7ff; border-radius: 5px; }" +
                "</style>" +
                "</head>" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333333;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td>" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse; max-width: 600px; background-color: #f9f9f9; border: 1px solid #dddddd; border-radius: 8px;\">" +
                
                // Header with logo (CID reference)
                "<tr><td align=\"center\" style=\"padding: 20px 0 5px 0;\">" +
                logoHtml +
                "</td></tr>" +
                
                // Add text logo for fallback
//                "<tr><td align=\"center\" style=\"padding: 5px 0 10px 0;\">" +
//                textLogoHtml +
//                "</td></tr>" +
                
                // Body
                "<tr><td style=\"padding: 20px 30px 20px 30px;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "Dear <strong>" + capitalizeFirstChar(firstName) + "</strong>," +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "We've received a request to reset the password for your account." +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "To proceed, please use the following OTP (One-Time Password) to reset your password:" +
                "</td></tr>" +
                "<tr><td style=\"padding: 15px 0 15px 0;\">" +
                "<div style=\"font-size: 14px; font-weight: bold; color: #000000; text-align: center; padding: 10px; background-color: #f0f7ff; border-radius: 5px;\">" +
                otp +
                "</div>" +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "<strong>Note:</strong> This OTP will expire in " + otpExpTime + " minutes. Please use it before it expires." +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "If you did not request this password reset, please ignore this email or contact our support team." +
                "</td></tr>" +
                "<tr><td style=\"padding: 20px 0 10px 0;\">" +
                "Best regards,<br/>" +
                "The VELINQ Team" +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +
                
                // Footer
                "<tr><td style=\"padding: 10px 30px 20px 30px; border-top: 1px solid #dddddd;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td style=\"font-size: 12px; color: #999999; text-align: center;\">" +
                "This is a system-generated email. Please do not reply to this message." +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +
                
                "</table>" +
                "</td></tr>" +
                "</table>" +
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
        Long linkExpTime=otpExpiredTimeInMinute;
        String verifyUserEmailUrl=UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/journal/public/verify-user")
                .queryParam("userName", userName)
                .queryParam("otp", otp)
                .toUriString();
        /*
        // Create a visually appealing text-based logo for fallback
        String textLogoHtml = "<div style=\"font-size: 36px; font-weight: bold; background-color: #0066a1; color: white; " +
                              "padding: 15px; border-radius: 10px; text-align: center; margin: 0 auto; max-width: 200px;\">" +
                              "VELINQ</div>";
        */
        // Use CID reference for logo - this will be replaced by EmailService
        String logoHtml = "<img src=\"cid:logo\" alt=\"VELINQ Logo\" width=\"100\" height=\"auto\" " +
                          "style=\"display: block; margin: 0 auto; max-width: 100px; border: 0; outline: none;\" />";

        String body = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />" +
                "<title>Verify Your Email</title>" +
                "<style type=\"text/css\">" +
                "body { margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333333; }" +
                ".container { max-width: 600px; }" +
                ".button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff !important; text-decoration: none; background-color: #007BFF; border-radius: 5px; }" +
                ".button:hover { background-color: #0056b3; }" +
                "</style>" +
                "</head>" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333333;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td>" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse; max-width: 600px; background-color: #f9f9f9; border: 1px solid #dddddd; border-radius: 8px;\">" +
                
                // Header with logo (CID reference)
                "<tr><td align=\"center\" style=\"padding: 20px 0 5px 0;\">" +
                logoHtml +
                "</td></tr>" +
                
                // Add text logo for fallback
//                "<tr><td align=\"center\" style=\"padding: 5px 0 10px 0;\">" +
//                textLogoHtml +
//                "</td></tr>" +
                
                // Body
                "<tr><td style=\"padding: 20px 30px 20px 30px;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "Dear <strong>" + capitalizeFirstChar(firstName) + "</strong>," +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 15px 0;\">" +
                "Thank you for signing up for Velinq! To complete your registration and verify your email address, please click the button below:" +
                "</td></tr>" +
                "<tr><td align=\"center\" style=\"padding: 15px 0 20px 0;\">" +
                "<a href=\"" + verifyUserEmailUrl + "\" style=\"display: inline-block; padding: 10px 25px; font-size: 16px; color: #ffffff; text-decoration: none; background-color: #28a745; border-radius: 5px;\">Verify Email</a>" +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "<strong>Note:</strong> This verification link will expire in " + linkExpTime + " minutes. Please verify your email promptly." +
                "</td></tr>" +
                "<tr><td style=\"padding: 20px 0 10px 0;\">" +
                "Best regards,<br/>" +
                "The VELINQ Team" +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +
                
                // Footer
                "<tr><td style=\"padding: 10px 30px 20px 30px; border-top: 1px solid #dddddd;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td style=\"font-size: 12px; color: #999999; text-align: center;\">" +
                "This is a system-generated email. Please do not reply to this message." +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +
                
                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body>" +
                "</html>";
                
        return body;
    }

    public boolean isValidSentiment(String journalSentiment) {
        if(journalSentiment==null || journalSentiment.trim().isEmpty()){
            return true; // Sentiment is optional, so null or empty is acceptable
        }

        try{
            Sentiment.valueOf(journalSentiment.toUpperCase());
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }

    public boolean isValidPrivacyLevel(String privacyLevel) {
        if(privacyLevel==null || privacyLevel.trim().isEmpty()){
            return true;  // Default would be PRIVATE
        }
        try{
            PrivacyLevel.valueOf(privacyLevel.trim().toUpperCase());
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }

    }

    public boolean isValidJournalCategory(String category) {
        if(category==null || category.trim().isEmpty()){
            return true;  // Default would be LIKE
        }
        try{
            JournalCategory.valueOf(category.trim().toUpperCase());
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }

    }

    public boolean isValidEmotionRating(Integer emotionRating) {
        if(emotionRating==null) return true; // default=1
        if(emotionRating>0 && emotionRating<=10) return true;
        return false;
    }

    public String getUserFullName(String firstName, String lastName) {
        return firstName.toUpperCase() + " "+lastName.toUpperCase();
    }

    public Integer getWordCount(String content) {
        if(content==null || content.trim().isEmpty()) return 0;
        return content.trim().split("\\s+").length;
    }

    public boolean isValidJournalSortBy(String sortBy) {
        if(sortBy==null || sortBy.trim().isEmpty()) return true;
        try{
            JournalSortBy.valueOf(sortBy.trim().toUpperCase());
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }

    public String getBodyForShareJournalNotification(String toUserEmail, JournalEntries journal, String currentUserEmail) {
        String logoHtml = "<img src=\"cid:logo\" alt=\"VELINQ Logo\" width=\"100\" height=\"auto\" " +
                "style=\"display: block; margin: 0 auto; max-width: 100px; border: 0; outline: none;\" />";

        String journalViewUrl = baseUrl + "/journal/journal/id/" + journal.getId();

        String authorName = journal.getAuthorName() != null ? journal.getAuthorName() : currentUserEmail;
        String recipientName = toUserEmail != null && !toUserEmail.isEmpty() ? toUserEmail.split("@")[0] : "User";

        String body = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />" +
                "<title>Journal Shared With You</title>" +
                "<style type=\"text/css\">" +
                "body { margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333333; }" +
                ".container { max-width: 600px; }" +
                ".button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff !important; text-decoration: none; background-color: #007BFF; border-radius: 5px; }" +
                ".button:hover { background-color: #0056b3; }" +
                "</style>" +
                "</head>" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333333;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td>" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse; max-width: 600px; background-color: #f9f9f9; border: 1px solid #dddddd; border-radius: 8px;\">" +

                // Header with logo
                "<tr><td align=\"center\" style=\"padding: 20px 0 5px 0;\">" +
                logoHtml +
                "</td></tr>" +

                // Body
                "<tr><td style=\"padding: 20px 30px 20px 30px;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "Hello <strong>" + capitalizeFirstChar(recipientName) + "</strong>," +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 15px 0;\">" +
                "<strong>" + authorName + "</strong> has shared a journal entry with you on Velinq!" +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "<strong>Title:</strong> " + journal.getTitle() +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "<strong>Content:</strong><br/>" +
                "<div style='background:#f4f8fb;padding:12px 15px;border-radius:5px;border:1px solid #e0e0e0;'>" +
                journal.getContent() +
                "</div>" +
                "</td></tr>" +
                "<tr><td align=\"center\" style=\"padding: 20px 0 20px 0;\">" +
                "<a href=\"" + journalViewUrl + "\" class=\"button\" style=\"display: inline-block; padding: 10px 25px; font-size: 16px; color: #ffffff; text-decoration: none; background-color: #007BFF; border-radius: 5px;\">View Journal</a>" +
                "</td></tr>" +
                "<tr><td style=\"padding: 0 0 10px 0;\">" +
                "You can now view, comment, or like this journal entry." +
                "</td></tr>" +
                "<tr><td style=\"padding: 20px 0 10px 0;\">" +
                "Best regards,<br/>" +
                "The VELINQ Team" +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +

                // Footer
                "<tr><td style=\"padding: 10px 30px 20px 30px; border-top: 1px solid #dddddd;\">" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">" +
                "<tr><td style=\"font-size: 12px; color: #999999; text-align: center;\">" +
                "This is a system-generated email. Please do not reply to this message." +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +

                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body>" +
                "</html>";

        return body;
    }

    public boolean isValidType(String type) {
        if(type==null || type.trim().isEmpty()) return true;
        try{
            UserEngagedByEnum.valueOf(type.trim().toUpperCase());
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }

    }

    // Helper to capitalize first character
//    private String capitalizeFirstChar(String str) {
//        if (str == null || str.isEmpty()) return "";
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
}
