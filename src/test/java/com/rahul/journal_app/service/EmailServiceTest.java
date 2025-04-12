package com.rahul.journal_app.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;


//    @Test
//    @Disabled
//    void testSendEmail(){
//        emailService.sendMail("rahul2140kumar@gmail.com",
//                "Testing java mail sender",
//                "Hi, Kya hal chal bhi");
//    }
}
