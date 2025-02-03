package com.rahul.journal_app.service;

import com.rahul.journal_app.controller.JournalEntryControllerV2;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.repository.JournalEntityRepository;
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

@Service
@Component
public class JournalEntryService {

    private static final Logger logger = LoggerFactory.getLogger(JournalEntryControllerV2.class);
    @Autowired
    private JournalEntityRepository journalEntityRepository;

    @Autowired
    private UserService userService;

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

            User updatedUser = userService.saveUserEntry(savedUser);
            logger.info("> updated journal into user dataDB: {}", updatedUser);
        }catch (Exception e){
            logger.error("An error occur during deleting the entry: {}", e.getMessage());
            throw new RuntimeException("An error occur during deleting the entry", e);
        }
    }

    @Transactional
    public JournalEntries saveJournalEntryOfUser(JournalEntries myJournal, String userName) {
        try{
            User savedUser = userService.findByUserName(userName);
            logger.info("> userName : {}, User Object :{}",userName, savedUser);
            if(!savedUser.isSentimentAnalysis() && myJournal.getSentiment()!=null && !myJournal.getSentiment().equals("")){
                savedUser.setSentimentAnalysis(true);
            }
            // save the journal into journal_entries database
            JournalEntries savedJournalEntry = saveJournalEntry(myJournal);
            logger.info("> updated journal into Journal dataDB: {}", savedJournalEntry);

            // save the journal id into user's journal list
            List<JournalEntries> userJournalEntities=savedUser.getJournalEntities();
            userJournalEntities.add(savedJournalEntry);
            User updatedUser=userService.saveUserEntry(savedUser);

            logger.info("> updated journal into user dataDB: {}", updatedUser);
            return savedJournalEntry;
        }catch (Exception e){
            logger.error("> Exception to save the journal of the user, Message: {}", e.getMessage());
            throw new RuntimeException("> Exception to save the journal of the user, Message: {}", e.getCause());
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
}


// controller --> service --> repository