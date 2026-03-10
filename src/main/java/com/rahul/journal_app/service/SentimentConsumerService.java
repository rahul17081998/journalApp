package com.rahul.journal_app.service;

import com.rahul.journal_app.model.SentimentalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SentimentConsumerService {

    @Autowired
    private EmailService emailService;


    @Value("${velinq.topic}")
    private String velinqTopic;
    @Value("${velinq.groupId1}")
    private String velinqGroupId1;
    @Value("${velinq.groupId2}")
    private String velinqGroupId2;


    @KafkaListener(topics = "${velinq.topic}", groupId = "${velinq.groupId1}")
    public void onConsume(SentimentalData sentimentalData){
        log.info("Message consume from kafka server. topic: [{}], groupId: [{}]", velinqTopic, velinqGroupId2);
        sendEmail(sentimentalData);
    }

//    @KafkaListener(topics = "${velinq.topic}", groupId = "${velinq.groupId2}")
//    public void onUpdate(SentimentalData sentimentalData){
//        log.info("Message consume from kafka server. topic: [{}], groupId: [{}]", velinqTopic, velinqGroupId1);
//        sendEmail(sentimentalData);
//    }

    private void sendEmail(SentimentalData sentimentalData) {
        log.info("Sending Email to user: {}", sentimentalData.getEmail());
        emailService.sendMail(sentimentalData.getEmail(), "Your Sentiment Analysis Report", sentimentalData.getSentiment());
    }
}
