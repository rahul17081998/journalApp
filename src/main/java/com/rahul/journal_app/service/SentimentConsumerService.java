package com.rahul.journal_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.journal_app.model.SentimentalData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${velinq.topic}", groupId = "${velinq.groupId1}")
    public void onConsume(ConsumerRecord<String, SentimentalData> record) {

        log.info("onConsume called, Topic={}, Partition={}", record.topic(), record.partition());
        log.debug("Key={}, Message={}", record.key(), record.value());
        try{
            sendEmail(record.value());
        }catch (Exception e){
            log.error("Error parsing message", e);

        }
    }


//    @KafkaListener(topics = "VELINQ.SENTIMENT.DEV", groupId = "aw1")
//    public void onUpdate(ConsumerRecord<String, String> record) {
//
//        log.info("Consumer=onUpdate | Topic={} | Partition={} | Key={} | Message={}",
//                record.topic(),
//                record.partition(),
//                record.key(),
//                record.value());
//    }


//    @KafkaListener(topics = "${velinq.topic}", groupId = "${velinq.groupId1}")
//    public void onConsume(SentimentalData sentimentalData){
//        log.info("onConsume: Message consume from kafka server. topic: [{}], groupId: [{}]", velinqTopic, velinqGroupId1);
////        sendEmail(sentimentalData);
//    }
//
//    @KafkaListener(topics = "${velinq.topic}", groupId = "${velinq.groupId1}")
//    public void onUpdate(SentimentalData sentimentalData){
//        log.info("onUpdate: Message consume from kafka server. topic: [{}], groupId: [{}]", velinqTopic, velinqGroupId1);
////        sendEmail(sentimentalData);
//    }

    private void sendEmail(SentimentalData sentimentalData) {
        log.info("Sending Email to user: {}", sentimentalData.getEmail());
        emailService.sendMail(sentimentalData.getEmail(), "Your Sentiment Analysis Report", sentimentalData.getSentiment());
    }
}
