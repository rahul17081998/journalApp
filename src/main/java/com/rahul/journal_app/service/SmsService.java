package com.rahul.journal_app.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class SmsService {

    @Value("${twilio.accountSID}")
    private String accountSid;
    @Value("${twilio.authToken}")
    private String authToken;
    @Value("${twilio.phoneNo}")
    private String twilioPhoneNo;

    @PostConstruct
    public void initTwilio(){
        Twilio.init(accountSid, authToken);
    }
    public ResponseEntity<?> sendWelcomeSMST(String toPhoneNumber) {

        try {
            sendSMS(toPhoneNumber, "Welcome to JournalApp!");
            log.info("SMS send to: {}", toPhoneNumber);
            return new ResponseEntity<>("SMS send to " + toPhoneNumber + " successfully", HttpStatus.OK);
        }catch (Exception e){
            log.warn("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void sendSMS(String toPhoneNumber, String body){
        Message message = Message.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(twilioPhoneNo),
                        body)
                .create();
        log.info("Twilio message SID: {}", message.getSid());
    }
}
