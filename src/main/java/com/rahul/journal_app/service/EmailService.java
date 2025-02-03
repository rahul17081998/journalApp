package com.rahul.journal_app.service;


import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    public void sendMail(String to, String subject, String body){
        try{
            MimeMessage message =javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body,true);
            javaMailSender.send(message);
        }catch (Exception e){
            log.error("Exception during sending a email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    public void sendMail2(String userName, String subject, String body) {
//        try{
//            MimeMessage message =javaMailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
//            mimeMessageHelper.setTo(userName);
//            mimeMessageHelper.setSubject(subject);
//            mimeMessageHelper.setText(body,true);
//            javaMailSender.send(message);
//        }catch (Exception e){
//            log.error("Exception during sending a email: {}", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
}
