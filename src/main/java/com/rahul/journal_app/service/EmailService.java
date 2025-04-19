package com.rahul.journal_app.service;


import com.rahul.journal_app.entity.Attachment;
import com.rahul.journal_app.exception.EmailSendingException;
import com.rahul.journal_app.repository.AttachmentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    
    @Autowired
    private AttachmentRepository attachmentRepository;
    
    @Value("${media.velinq.logo}")
    private String velinqLogoId;

    /**
     * Sends a regular HTML email
     */
    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("Exception during sending a email to {}: {}", to, e.getMessage(), e);
            throw new EmailSendingException("Failed to send email to " + to, e);
        }
    }


    public void sendEmailWithEmbeddedLogo(String to, String subject, String htmlContent){

        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // send as HTML

            boolean logoAttached = false;

            // Step 1: Try loading from classpath (static resources)
            ClassLoader classLoader = getClass().getClassLoader();
            try (var inputStream = classLoader.getResourceAsStream("static/velinklogo1.png")) {
                if (inputStream != null) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    helper.addInline("logo", new ByteArrayResource(imageBytes), "image/png");
                    log.info("Loaded VELINQ logo from resources for email to {}", to);
                    logoAttached = true;
                } else {
                    log.warn("VELINQ logo not found in resources/static directory.");
                }
            } catch (Exception e) {
                log.error("Failed to load logo from resources: {}", e.getMessage(), e);
            }

            // Step 2: Fallback to database if logo not found in classpath
            if(!logoAttached && velinqLogoId!=null){
                try{
                    ObjectId logoId = new ObjectId(velinqLogoId);
                    Attachment attachment= attachmentRepository.findById(logoId).orElse(null);
                    if(attachment!=null && attachment.getData()!=null && attachment.getData().length>0){
                        helper.addInline("logo", new ByteArrayResource(attachment.getData()), attachment.getFileType());
                        log.info("Embedded VELINQ logo ({} bytes) for email to {}", attachment.getData().length, to);
                        logoAttached=true;
                    }else{
                        log.warn("VELINQ logo not found or empty for ID: {}", velinqLogoId);
                    }
                }catch (Exception e){
                    log.error("Error loading VELINQ logo from DB: {}", e.getMessage(), e);
                }
            }

            if (!logoAttached) {
                log.warn("No VELINQ logo was embedded in the email to {}", to);
            }

            javaMailSender.send(message);
            log.info("Email with embedded logo sent to {}", to);

        }catch (Exception e){
            log.error("Exception during sending email with logo to {}: {}", to, e.getMessage(), e);
            throw new EmailSendingException("Failed to send email to " + to, e);
        }
    }
}
