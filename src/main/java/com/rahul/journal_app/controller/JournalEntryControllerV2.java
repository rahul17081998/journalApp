package com.rahul.journal_app.controller;

import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.service.JournalEntryService;
import com.rahul.journal_app.service.UserService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/journal")
public class JournalEntryControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(JournalEntryControllerV2.class);


    private final JournalEntryService journalEntryService;

    private final UserService userService;

    public JournalEntryControllerV2(JournalEntryService journalEntryService, UserService userService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<JournalEntries>> getAllJournalsEntriesOfUser(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String userName= authentication.getName();
        User user = userService.findByUserName(userName);
        logger.info("> user name: {}", authentication.getName());

        logger.info("Fetching all journals");
        List<JournalEntries> allJournal= user.getJournalEntities();
        if(allJournal !=null && !allJournal.isEmpty()){
            logger.info("> No of journals: {}", allJournal.size());
            return new ResponseEntity<>(allJournal, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create-journal")
    public ResponseEntity<?> createJournal(@RequestBody JournalEntries myJournal){
        logger.info("Creating new journal");
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String userName= authentication.getName();
        JournalEntries savedJournalEntry = null;
        try{
            savedJournalEntry=journalEntryService.saveJournalEntryOfUser(myJournal, userName);
            if(savedJournalEntry==null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        }catch (Exception e){
            logger.info("Exception occur while creating a journal entry, message: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return new ResponseEntity<>(savedJournalEntry, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getJournalById(@PathVariable("id") ObjectId id){
        logger.info("Fetching journal with id: {}", id);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            Optional<JournalEntries> optionalJournalEntity = Optional.empty();
            if(journalEntryService.isJournalIdContainUser(id, userName)){
                optionalJournalEntity=journalEntryService.findJournalById(id);
            }

            if (optionalJournalEntity.isPresent()) {
                return new ResponseEntity<>(optionalJournalEntity.get(), HttpStatus.OK);
            } else {
                logger.warn("User [{}] does not have a journal with id [{}]", userName, id);
                return new ResponseEntity<>("User "+userName+" doesn't have journal id "+id.toString(), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            // Log the exception and return a 500 response
            logger.error("An error occurred while fetching journal by id [{}]: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("An internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteJournal(@PathVariable("id") ObjectId id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        try{
            if(journalEntryService.isJournalIdContainUser(id, userName)){
                logger.info("user : {} have journal id: {} ", userName, id);
                journalEntryService.deleteJournalByIdOfUser(userName, id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else{
                logger.info("user : {} don't have journal id: {} ", userName, id);
                return new ResponseEntity<>("user don't have given journal id", HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            logger.error("An error occurred while fetching journal by id [{}]: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("An internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateJournal(@PathVariable("id") ObjectId id,
                                                        @RequestBody JournalEntries newJournal)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        if(journalEntryService.isJournalIdContainUser(id, userName)){
            try{
                Optional<JournalEntries> optionalJournalEntity = journalEntryService.findJournalById(id);
                JournalEntries oldJournal = optionalJournalEntity.orElse(null);
                if(oldJournal!=null){
                    logger.info("Updating the journal");
                    oldJournal.setTitle(newJournal.getTitle()!=null && !newJournal.getTitle().equals("")? newJournal.getTitle() : oldJournal.getTitle());
                    oldJournal.setContent(newJournal.getContent()!=null && !newJournal.getContent().equals("")? newJournal.getContent() : oldJournal.getContent());
                    journalEntryService.saveJournalEntry(oldJournal);
                    return new ResponseEntity<>(oldJournal, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }catch (Exception e){
                logger.error("An error occurred while updating a journal by id [{}]: {}", id, e.getMessage(), e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("User don't have the given journal", HttpStatus.NOT_FOUND);
    }
}
