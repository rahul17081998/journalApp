package com.rahul.journal_app.controller;

import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.exception.AccessDeniedException;
import com.rahul.journal_app.exception.BadRequestException;
import com.rahul.journal_app.exception.ResourceNotFoundException;
import com.rahul.journal_app.model.ApiResponse;
import com.rahul.journal_app.model.JournalEntryDTO;
import com.rahul.journal_app.model.request.JournalEntityRequest;
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
    public ResponseEntity<ApiResponse<List<JournalEntryDTO>>> fetchAllJournalsOfUser(
            @RequestParam(required = false) Boolean isShared,
            @RequestParam(required = false) Boolean isFavourite,
            @RequestParam(required = false) Boolean isMyCreated,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String privacyLevel,
            @RequestParam(required = false) String sortBy
    ){

        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String userName= authentication.getName();
        log.info("Received request to fetch all journal entries for user: {}", userName);

        if(!util.isValidPrivacyLevel(privacyLevel)){
            log.info("Invalid privacy level {}", privacyLevel);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_PRIVACY_LEVEL_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidJournalCategory(category)) {
            log.info("Invalid category value {}", category);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_JOURNAL_CATEGORY_ERROR, HttpStatus.BAD_REQUEST));
        }else if(!util.isValidJournalSortBy(sortBy)){
            log.info("Invalid sortBy  value {}", category);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_JOURNAL_SORT_BY_VALUE_ERROR, HttpStatus.BAD_REQUEST));
        }

        try {
            List<JournalEntryDTO> journalListDTO = journalEntryService.getAllUserJournals(
                    isShared, isFavourite, isMyCreated, category, privacyLevel, sortBy, userName
                    );

            log.info("Fetched journal entries for user: {} - Entry count: {}", userName, journalListDTO != null ? journalListDTO.size() : 0);
            if (journalListDTO != null && !journalListDTO.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success(journalListDTO, Constants.USER_JOURNALS_FETCH_SUCCESSFULLY_MSG));
            }

            log.info("No journal entries found for user: {}", userName);
            return ResponseEntity.ok(ApiResponse.success(new ArrayList<>(), Constants.NO_JOURNAL_FOUND_MATCHING_THE_CRITERIA_MSG));

        }catch (Exception e){
            log.error("Exception occurred while fetching journal entries for user [{}]: {}", userName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(ErrorCode.FAILED_TO_FETCH_USER_JOURNAL_ENTRIES, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }

    }
//    @GetMapping()
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
            return ResponseEntity.ok(ApiResponse.success(new ArrayList<>(), Constants.NO_JOURNAL_FOUND_MATCHING_THE_CRITERIA_MSG));

        }catch (Exception e){
            log.error("Exception occurred while fetching journal entries for user [{}]: {}", userName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(ErrorCode.FAILED_TO_FETCH_USER_JOURNAL_ENTRIES, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /*
    JournalEntryControllerV2.java Updates:
    > Update createJournal method
    > Add fields for authorEmail, authorName, category, mood, emotionRating
    > Initialize privacyLevel to PRIVATE and empty lists for comments/likedByUsers
     */
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
        else if(!util.isValidPrivacyLevel(myJournal.getPrivacyLevel())){
            log.info("Journal creation failed: Invalid privacy level {}", myJournal.getPrivacyLevel());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_PRIVACY_LEVEL_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidJournalCategory(myJournal.getCategory())) {
            log.info("Journal creation failed: Invalid category value {}", myJournal.getCategory());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_JOURNAL_CATEGORY_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidEmotionRating(myJournal.getEmotionRating())){
            log.info("Journal creation failed: Invalid Emotion Rating value {}", myJournal.getEmotionRating());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_EMOTION_RATING_ERROR, HttpStatus.BAD_REQUEST));
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

    /*
    3. Update getJournalById method
        Modify to include view count increment when journal is viewed
        Check if requesting user can access based on privacyLevel
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<JournalEntryDTO>> getJournalById(@PathVariable("id") String id){
        log.info("Request received to fetch journal with ID: {}", id);
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            JournalEntryDTO journal = journalEntryService.getJournalWithAccessControl(id, userName);
            return ResponseEntity.ok(ApiResponse.success(journal, Constants.JOURNAL_ENTRY_FETCH_SUCCESSFULLY_MSG));

        }catch (ResourceNotFoundException e){
            log.warn("Journal not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ErrorCode.JOURNAL_ENTRY_NOT_FOUND_IN_DB, e.getMessage(), HttpStatus.NOT_FOUND));
        }catch (AccessDeniedException e){
            log.warn("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.ACCESS_DENIED, e.getMessage(), HttpStatus.NOT_FOUND));

        } catch (Exception e){
            logger.error("Exception while fetching journal entry with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.EXCEPTION_WHILE_FETCHING_JOURNAL_ENTRY, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    // A journal entry can only be deleted by its original autor
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



    /*
    Update updateJournal method
    > Extend to support updating new fields (category, mood, emotionRating)
    > Ensure privacy settings are properly validated
    > A journal entry can only be updated by its original author
     */
    @PutMapping("/id/{id}")
    public ResponseEntity<ApiResponse<JournalEntries>> updateJournal(@PathVariable("id") String idStr, @RequestBody JournalEntityRequest request)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        if(StringUtils.isBlank(idStr)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.JOURNAL_OBJECT_ID_MISSING, HttpStatus.BAD_REQUEST));
        }
        else if(request.getTitle()!=null && request.getTitle().isEmpty()){
            log.warn("Journal update failed: Title is blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.TITLE_EMPTY_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(request.getContent()!=null && request.getContent().isEmpty()){
            log.warn("Journal update failed: Content is blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.CONTENT_EMPTY_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidSentiment(request.getSentiment())){
            log.warn("Journal update failed: Invalid sentiment {}", request.getSentiment());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_SENTIMENT_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidPrivacyLevel(request.getPrivacyLevel())){
            log.info("Journal update failed: Invalid privacy level {}", request.getPrivacyLevel());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_PRIVACY_LEVEL_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidJournalCategory(request.getCategory())) {
            log.info("Journal update failed: Invalid category value {}", request.getCategory());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_JOURNAL_CATEGORY_ERROR, HttpStatus.BAD_REQUEST));
        }
        else if(!util.isValidEmotionRating(request.getEmotionRating())){
            log.info("Journal update failed: Invalid Emotion Rating value {}", request.getEmotionRating());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.INVALID_EMOTION_RATING_ERROR, HttpStatus.BAD_REQUEST));
        }


        ObjectId id;
        try{
            id=new ObjectId(idStr);
            ResponseEntity<ApiResponse<JournalEntries>> response=journalEntryService.updateJournalEntry(request, id, userName);
            return response;
        }catch (Exception e){
            log.error("Exception while updating journal entry for user: {} : {}", userName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(ErrorCode.FAILED_TO_UPDATE_JOURNAL_ENTRY, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/share-journal")
    public ResponseEntity<ApiResponse<String>> shareJournal(
            @RequestParam(required = true) String toEmail,
            @RequestParam(required = true) String journalId)
    {

        log.info("Request received to share journal: {} with user: {}", journalId, toEmail);
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.shareJournal(journalId, userName, toEmail);
            return ResponseEntity.ok(ApiResponse.success(Constants.JOURNAL_ENTRY_SHARED_SUCCESSFULLY_MSG));

        }catch (ResourceNotFoundException e){
            log.warn("Journal not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ErrorCode.JOURNAL_ENTRY_NOT_FOUND_IN_DB, e.getMessage(), HttpStatus.NOT_FOUND));
        }catch (AccessDeniedException e){
            log.warn("Access denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.ACCESS_DENIED_TO_SHARE, e.getMessage(), HttpStatus.BAD_REQUEST));

        } catch (BadRequestException e){
            log.warn("Bad request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.SENDER_AND_RECEIVER_ARE_SAME, e.getMessage(), HttpStatus.BAD_REQUEST));

        }
        catch (Exception e){
            logger.error("Exception while fetching journal entry with id {}: {}", journalId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.EXCEPTION_WHILE_SHARING_JOURNAL_ENTRY, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
