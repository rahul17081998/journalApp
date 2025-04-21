package com.rahul.journal_app.service;

import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.exception.BadRequestException;
import com.rahul.journal_app.exception.InternalServerErrorException;
import com.rahul.journal_app.model.ApiResponse;
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
    public ResponseEntity<ApiResponse<String>> sendWelcomeSMST(String toPhoneNumber) {


        if(!isValidPhoneNumber( toPhoneNumber)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(ErrorCode.INVALID_PHONE_NUMBER, HttpStatus.BAD_REQUEST));
        }

        toPhoneNumber= "+91" + toPhoneNumber; // starts with +91XXX...

        try {
            sendSMS(toPhoneNumber, "Kiske liye kam karte ho tum😂 batao \n Sir mai kam hi nhi karta hu kuch🤣🤣!");
            log.info("SMS send to: {}", toPhoneNumber);
            String message = "SMS send to " + toPhoneNumber + " successfully";
            return ResponseEntity.ok(ApiResponse.success(message));
        }catch (Exception e){
            log.warn("Error {}", e.getMessage(), e);
            throw new InternalServerErrorException(ErrorCode.FAILED_TO_SEND_MSG,e.getCause());
        }
    }

    private boolean isValidPhoneNumber(String number){
        if(number==null || number.isEmpty() || number.length()<10 || number.length()>10) return false;
        return number.matches("^[6-9]\\d{9}");
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
