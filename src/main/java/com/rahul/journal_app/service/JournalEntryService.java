package com.rahul.journal_app.service;

import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.controller.JournalEntryControllerV2;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.enums.Sentiment;
import com.rahul.journal_app.exception.InternalServerErrorException;
import com.rahul.journal_app.model.request.JournalEntityRequest;
import com.rahul.journal_app.model.response.JournalEntryResponse;
import com.rahul.journal_app.repository.JournalEntityRepository;
import com.rahul.journal_app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Component
public class JournalEntryService {

    private static final Logger logger = LoggerFactory.getLogger(JournalEntryControllerV2.class);
    @Autowired
    private JournalEntityRepository journalEntityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public JournalEntries saveJournalEntry(JournalEntries journalEntity){
        if(journalEntity.getDate() ==null){
            LocalDateTime now = LocalDateTime.now();
            Date localDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            journalEntity.setDate(localDate);
        }
        JournalEntries savedJournal = journalEntityRepository.save(journalEntity);
        return savedJournal;
    }

    public List<JournalEntries> getAllJournal(){
        List<JournalEntries> allJournals= journalEntityRepository.findAll();
        return allJournals;
    }

    public Optional<JournalEntries> findJournalById(ObjectId id) {
        return journalEntityRepository.findById(id);
    }

    @Transactional
    public void deleteJournalByIdOfUser(String userName, ObjectId id) {
        try {
            User savedUser = userService.findByUserName(userName);
            logger.info("> userName : {}, User Object :{}", userName, savedUser);
            // Delete the journal into journal_entries database
            JournalEntries journalEntries = findJournalById(id).get();
            journalEntityRepository.deleteById(id);

            // Delete the journal id into user's journal list
            List<JournalEntries> userJournalEntities = savedUser.getJournalEntities();
            userJournalEntities.removeIf(journal -> journal.equals(journalEntries));
            savedUser.setJournalEntities(userJournalEntities);

//            User updatedUser = userService.saveUserEntry(savedUser);

            User updatedUser= userRepository.save(savedUser);
            logger.info("> updated journal into user dataDB: {}", updatedUser);
        }catch (Exception e){
            logger.error("An error occur during deleting the entry: {}", e.getMessage());
            throw new RuntimeException("An error occur during deleting the entry", e);
        }
    }

    @Transactional
    public JournalEntries saveJournalEntryOfUser(JournalEntityRequest myJournal, String userName) {

        log.info("Saving journal entry for user: {}", userName);
        JournalEntries journalEntries=JournalEntries.builder()
                .title(myJournal.getTitle())
                .content(myJournal.getContent())
                .sentiment((myJournal.getSentiment()!=null && !myJournal.getSentiment().trim().isEmpty())? Sentiment.valueOf(myJournal.getSentiment().trim().toUpperCase()) :null)
                .build();

        try{
            User savedUser = userService.findByUserName(userName);
            log.debug("Fetched user-details for journal saving: {}", savedUser);

            if(!savedUser.isSentimentAnalysis() && journalEntries.getSentiment()!=null){
                savedUser.setSentimentAnalysis(true);
                log.info("Enabled sentiment analysis for user: {}", userName);
            }

            JournalEntries savedJournalEntry = saveJournalEntry(journalEntries);
            log.info("Saved journal entry to journal DB: {}", savedJournalEntry);

            List<JournalEntries> userJournalEntities=savedUser.getJournalEntities();
            userJournalEntities.add(savedJournalEntry);

            User updatedUser=userRepository.save(savedUser);
            log.info("Updated user's journal list: {}", updatedUser.getJournalEntities());

            return savedJournalEntry;
        }catch (Exception e){
            log.error("Exception while saving journal for user {}: {}", userName, e.getMessage(), e);
            throw new InternalServerErrorException(ErrorCode.EXCEPTION_WHILE_ADDING_JOURNAL_ENTRY, e.getCause());
        }

    }

    public boolean isJournalIdContainUser(ObjectId journalId, String userName){
        User user = userService.findByUserName(userName);
        for (var journal : user.getJournalEntities()) {
            if (journal.getId().equals(journalId)) {
                logger.info("> user [{}] have a journal whose id is: [{}]", userName, journal.getId());
                return true;
            }
        }
        return false;
    }

    public JournalEntryResponse getJournalEntry(ObjectId id) {
        Optional<JournalEntries> optionalJournalEntry=journalEntityRepository.findById(id);
        if(optionalJournalEntry.isEmpty()){
            log.warn("Journal entry with id {} not found", id);
            return null;
        }

        JournalEntries journalEntries = optionalJournalEntry.get();
        return JournalEntryResponse.builder()
                .title(journalEntries.getTitle())
                .content(journalEntries.getContent())
                .sentiment(journalEntries.getSentiment().name())
                .date(journalEntries.getDate())
                .build();
    }
}


// controller --> service --> repository