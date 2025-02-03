package com.rahul.journal_app.scheduler;

import com.rahul.journal_app.enums.Sentiment;
import com.rahul.journal_app.cache.AppCache;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.model.SentimentalData;
import com.rahul.journal_app.repository.UserRepositoryImpl;
import com.rahul.journal_app.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserScheduler {



    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentalData> kafkaTemplate;


    @Scheduled(cron = "0 0 9 * * SUN")
//    @Scheduled(cron = "0 * * ? * *")
    public void fetchUserAndSendSaMail(){
        log.info("Scheduler started");
        List<User> users =userRepositoryImpl.getUsersForSentimentAnalysis();
        for(User user: users){
            List<JournalEntries> journalEntriesList=user.getJournalEntities();
            LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

            List<Sentiment> sentimentList = journalEntriesList.stream()
                    .filter(entry -> {
                        Date date = entry.getDate(); // Assuming getDate() returns java.util.Date
                        LocalDateTime entryDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        return entryDate.isAfter(oneWeekAgo);
                    })
                    .map(x->x.getSentiment())
                    .collect(Collectors.toList());

            Sentiment mostFrequentSentiment=findMostFrequentSentiment(sentimentList);
            if (mostFrequentSentiment != null) {
                String subject = "Your Sentiment Analysis Report";
                String body = "Dear " + user.getUserName() + ",\n\n" +
                        "We have analyzed your content and observed that the predominant sentiment in your posts is: " + mostFrequentSentiment.toString() + ".\n\n" +
                        "Thank you for using our platform, and we hope to continue providing valuable insights to you.\n\n" +
                        "Best regards,\n" +
                        "The Sentiment Analysis Team\n" +
                        "Rahul Kumar";
//                emailService.sendMail(user.getEmail(), subject, body);

                SentimentalData sentimentalData = SentimentalData.builder()
                        .email(user.getUserName())
                        .sentiment(body)
                        .build();
                try {
                    kafkaTemplate.send("weekly-sentiments", sentimentalData.getEmail(), sentimentalData);
                }catch (Exception e){
                    // fall back, if kafka not working
                    emailService.sendMail(sentimentalData.getEmail(), subject, body);
                }
            }
        }
    }



    @Scheduled(cron="*/10 * * * * *")
    public void clearCache(){
        appCache.init();

    }

    public Sentiment findMostFrequentSentiment(List<Sentiment> sentimentList) {
        if (sentimentList == null || sentimentList.isEmpty()) {
            return null; // Return null if the list is empty or null
        }

        // Map to store sentiment counts
        Map<Sentiment, Integer> sentimentCount = new HashMap<>();

        // Count each sentiment's occurrences
        for (Sentiment sentiment : sentimentList) {
            if (sentiment != null) {
                sentimentCount.put(sentiment, sentimentCount.getOrDefault(sentiment, 0) + 1);
            }
        }
        // Find the sentiment with the highest count
        Sentiment mostFrequentSentiment = null;
        int maxCount = 0;
        for (Map.Entry<Sentiment, Integer> entry : sentimentCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostFrequentSentiment = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mostFrequentSentiment;
    }
}
