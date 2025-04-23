package com.rahul.journal_app.controller;

import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.model.ApiResponse;
import com.rahul.journal_app.model.request.JournalEntityRequest;
import com.rahul.journal_app.model.response.JournalEntryResponse;
import com.rahul.journal_app.service.JournalEntryService;
import com.rahul.journal_app.service.UserService;
import com.rahul.journal_app.utils.Util;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@Slf4j
@RestController
@RequestMapping(path = "/journal")
public class JournalEntryControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(JournalEntryControllerV2.class);


    private final JournalEntryService journalEntryService;

    private final UserService userService;

    private final Util util;

    public JournalEntryControllerV2(JournalEntryService journalEntryService, UserService userService, Util util) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
        this.util = util;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<JournalEntries>>> getAllJournalsEntriesOfUser(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String userName= authentication.getName();
        log.info("Received request to fetch all journal entries for user: {}", userName);

        try {

            User user = userService.findByUserName(userName);
            if (user == null) {
                logger.warn("User not found: {}", userName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(ErrorCode.USER_NOT_FOUND, Constants.USER_NOT_FOUND_MSG, HttpStatus.NOT_FOUND));
            }

            List<JournalEntries> journalList = user.getJournalEntities();
            log.info("Fetched journal entries for user: {} - Entry count: {}", userName, journalList != null ? journalList.size() : 0);

            if (journalList != null && !journalList.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success(journalList, Constants.USER_JOURNALS_FETCH_SUCCESSFULLY_MSG));
            }

            log.info("No journal entries found for user: {}", userName);
            return ResponseEntity.ok(ApiResponse.success(new ArrayList<>(), Constants.JOURNAL_NOT_FOUND_MSG));

        }catch (Exception e){
            log.error("Exception occurred while fetching journal entries for user [{}]: {}", userName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(ErrorCode.FAILED_TO_FETCH_USER_JOURNAL_ENTRIES, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/create-journal")
    public ResponseEntity<ApiResponse<JournalEntries>> createJournal(@RequestBody JournalEntityRequest myJournal){
        logger.info("Received request to create a new journal entry");
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String userName= authentication.getName();

        if(StringUtils.isBlank(myJournal.getTitle())){
            log.warn("Journal creation failed: Title is blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.TITLE_EMPTY_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(StringUtils.isBlank(myJournal.getContent())){
            log.warn("Journal creation failed: Content is blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.CONTENT_EMPTY_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidSentiment(myJournal.getSentiment())){
            log.warn("Journal creation failed: Invalid sentiment {}", myJournal.getSentiment());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_SENTIMENT_ERROR, HttpStatus.BAD_REQUEST));
        }


        try{
            JournalEntries savedJournalEntry=journalEntryService.saveJournalEntryOfUser(myJournal, userName);
            if(savedJournalEntry==null){
                log.error("Failed to save journal entry for user: {}", userName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.FAILED_TO_CREATE_AND_SAVE_JOURNAL_ENTRY, HttpStatus.BAD_REQUEST));
            }

            log.info("Journal entry created successfully for user: {}", userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedJournalEntry, Constants.JOURNAL_ENTRY_CREATED_SUCCESSFULLY_MSG, HttpStatus.CREATED));

        }catch (Exception e){
            log.error("Exception while creating journal entry for user: {}", userName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.EXCEPTION_WHILE_ADDING_JOURNAL_ENTRY, HttpStatus.INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<JournalEntryResponse>> getJournalById(@PathVariable("id") ObjectId id){
        log.info("Fetching journal with id: {}", id);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            JournalEntryResponse response= journalEntryService.getJournalEntry(id);

            if(response==null){
                log.warn("Journal entry with id {} not found.", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_JOURNAL_ID_ERROR, HttpStatus.BAD_REQUEST));
            }

            log.info("Successfully fetched journal entry with id: {}", id);
            return ResponseEntity.ok(ApiResponse.success(response, Constants.JOURNAL_ENTRY_FETCH_SUCCESSFULLY_MSG));

        }catch (Exception e){
            logger.error("Exception while fetching journal entry with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.EXCEPTION_WHILE_FETCHING_JOURNAL_ENTRY, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    // A journal entry can only be deleted by its original author
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


    // A journal entry can only be updated by its original author
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
