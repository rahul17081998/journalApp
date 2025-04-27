package com.rahul.journal_app.service;

import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.controller.JournalEntryControllerV2;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.enums.JournalCategory;
import com.rahul.journal_app.enums.JournalSortBy;
import com.rahul.journal_app.enums.PrivacyLevel;
import com.rahul.journal_app.enums.Sentiment;
import com.rahul.journal_app.exception.*;
import com.rahul.journal_app.mapper.JournalEntityMapper;
import com.rahul.journal_app.model.ApiResponse;
import com.rahul.journal_app.model.JournalEntryDTO;
import com.rahul.journal_app.model.request.JournalEntityRequest;
import com.rahul.journal_app.model.response.JournalEntryResponse;
import com.rahul.journal_app.repository.JournalEntityRepository;
import com.rahul.journal_app.repository.UserRepository;
import com.rahul.journal_app.utils.Util;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private JournalEntityMapper journalEntityMapper;

    @Autowired
    private Util util;
    @Autowired
    private EmailService emailService;

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

        try{
            User savedUser = userService.findByUserName(userName);
            log.debug("Fetched user-details for journal saving: {}", savedUser);

            JournalEntries journalEntries=JournalEntries.builder()
                    .title(myJournal.getTitle())
                    .content(myJournal.getContent())
                    .sentiment((myJournal.getSentiment()!=null && !myJournal.getSentiment().trim().isEmpty())? Sentiment.valueOf(myJournal.getSentiment().trim().toUpperCase()) :null)
                    .authorEmail(userName)
                    .authorName(util.getUserFullName(savedUser.getFirstName(), savedUser.getLastName()))
                    .category((myJournal.getCategory()!=null && !myJournal.getCategory().trim().isEmpty())? JournalCategory.valueOf(myJournal.getCategory().trim().toUpperCase()): JournalCategory.DAILY)
                    .mood(myJournal.getMood()==null? "": myJournal.getMood().trim().toUpperCase())
                    .emotionRating(myJournal.getEmotionRating()==null? 1: myJournal.getEmotionRating())
                    .wordCount(util.getWordCount(myJournal.getContent()))
                    .privacyLevel((myJournal.getPrivacyLevel()!=null && !myJournal.getPrivacyLevel().trim().isEmpty())? PrivacyLevel.valueOf(myJournal.getPrivacyLevel().trim().toUpperCase()): PrivacyLevel.PRIVATE )
                    .build();

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


    @Transactional
    public ResponseEntity<ApiResponse<JournalEntries>> updateJournalEntry(JournalEntityRequest request, ObjectId id, String userName) {
        log.info("Updating journal entry : {}", id.toString());
        Optional<JournalEntries> optionalSavedJournal=journalEntityRepository.findById(id);
        if(!optionalSavedJournal.isPresent()){
            log.info("Journal entry not found with id {} in the db", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ErrorCode.JOURNAL_ENTRY_NOT_FOUND_IN_DB, HttpStatus.NOT_FOUND));
        }

        try {
            JournalEntries dbJournal = optionalSavedJournal.get();
            if (dbJournal.getAuthorEmail()==null || !dbJournal.getAuthorEmail().equalsIgnoreCase(userName)) {
                String message = "User " +userName+" is not allowed to update this journal, Author: "+dbJournal.getAuthorEmail();
                log.info("User {} is not allowed to update this journal, Author: {}", userName, dbJournal.getAuthorEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error(ErrorCode.JOURNAL_UPDATE_UNAUTHORIZED, message, HttpStatus.BAD_REQUEST));
            }

            dbJournal.setTitle(StringUtils.isBlank(request.getTitle()) ? dbJournal.getTitle() : request.getTitle().trim());
            dbJournal.setContent(StringUtils.isBlank(request.getContent()) ? dbJournal.getContent() : request.getContent().trim());
            dbJournal.setSentiment(StringUtils.isBlank(request.getSentiment()) ? dbJournal.getSentiment() : Sentiment.valueOf(request.getSentiment().trim().toUpperCase()));
            dbJournal.setCategory(StringUtils.isBlank(request.getCategory()) ? dbJournal.getCategory() : JournalCategory.valueOf(request.getCategory().trim().toUpperCase()));
            dbJournal.setMood(StringUtils.isBlank(request.getMood()) ? dbJournal.getMood() : request.getMood().trim().toUpperCase());
            dbJournal.setEmotionRating(request.getEmotionRating() == null ? dbJournal.getEmotionRating() : request.getEmotionRating());
            dbJournal.setWordCount(StringUtils.isBlank(request.getContent()) ? dbJournal.getWordCount() : util.getWordCount(request.getContent().trim()));
            dbJournal.setPrivacyLevel(StringUtils.isBlank(request.getPrivacyLevel()) ? dbJournal.getPrivacyLevel() : PrivacyLevel.valueOf(request.getPrivacyLevel().trim().toUpperCase()));

            JournalEntries updatedEntry=journalEntityRepository.save(dbJournal);

            log.info("Journal entry updated successfully: {}", id.toString());
            return ResponseEntity.ok(ApiResponse.success(updatedEntry, Constants.JOURNAL_ENTRY_UPDATED_SUCCESSFULLY_MSG));

        }catch (Exception e){
            log.error("Exception while updating journal entry for user: {}: {}", userName, e.getMessage());
            throw new InternalServerErrorException(ErrorCode.EXCEPTION_WHILE_UPDATING_JOURNAL_ENTRY, e.getCause());
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

    @Transactional
    public void shareJournal(String journalId, String currentUserEmail, String toUserId){
//        try {
            ObjectId id = validateAndConvertId(journalId);
            if(toUserId.trim().toLowerCase().equalsIgnoreCase(currentUserEmail)){
                throw new BadRequestException("Sender: "+currentUserEmail+" and receiver: " +toUserId+" emails can not be same");
            }
            JournalEntries journal = journalEntityRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: +" + journalId));

            validateAccess(journal, currentUserEmail);
            if (journal.getPrivacyLevel() != null && journal.getPrivacyLevel() == PrivacyLevel.PRIVATE) {
                log.info("Mark Journal private to shared: {}", journalId);
                journal.setPrivacyLevel(PrivacyLevel.SHARED);
            }
            journal.getSharedWithUserEmail().add(toUserId);
            log.info("Save journal {} in DB", journalId);
            journalEntityRepository.save(journal);
            User sharedUser = userRepository.findByUserName(toUserId);
            if (sharedUser == null) {
                throw new ResourceNotFoundException("User not found with email: " + toUserId);
            }
            log.info("Save journal id {} to user {} table", journalId, toUserId);
            sharedUser.getSharedJournalIds().add(journal.getId());
            userRepository.save(sharedUser);
            // Push a notification email to an usr
            sendShareNotification(toUserId, journal, currentUserEmail);
            log.info("Journal {}  successfully shared with user: {}", journalId, toUserId);
//        }catch (Exception e){
//            log.error("Some Exception occurred while sharing journal: {}", journalId);
//            throw new SharingJournalException("Some Exception occurred while sharing journal: "+journalId+" with user: "+toUserId);
//        }

    }

    private void sendShareNotification(String toUserId, JournalEntries journal, String currentUserEmail) {

        try {
            String htmlBody = util.getBodyForShareJournalNotification(toUserId, journal, currentUserEmail);
            emailService.sendEmailWithEmbeddedLogo(toUserId, currentUserEmail + "share a journal with you", htmlBody);
        }catch (Exception e){
            throw new EmailSendingException("Failed to send notification email to" +toUserId, e.getCause());
        }
    }

    public JournalEntryDTO getJournalWithAccessControl(String idStr, String currentUserEmail){
        ObjectId id= validateAndConvertId(idStr);
        JournalEntries journal=journalEntityRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Journal entry not found with id: "+idStr));

        validateAccess(journal, currentUserEmail);

        incrementViewCount(journal, currentUserEmail);

        return journalEntityMapper.toDTO(journal);
    }

    private void incrementViewCount(JournalEntries journal, String currentUserEmail) {
        // Optionally skip author's view count
        String authorEmail = journal.getAuthorEmail();
        if(authorEmail!=null && !authorEmail.equalsIgnoreCase(currentUserEmail)){
            journal.setViewCount(journal.getViewCount()+1);
            journalEntityRepository.save(journal);
            log.info("Incremented view count for journal {} to {}", journal.getId(), journal.getViewCount());
        }
    }

    private void validateAccess(JournalEntries journal, String currentUserEmail) {
        boolean hasAccess = false;
        if(journal.getPrivacyLevel()!=null && journal.getPrivacyLevel()==PrivacyLevel.PRIVATE){
            hasAccess= journal.getAuthorEmail()!=null && journal.getAuthorEmail().equalsIgnoreCase(currentUserEmail);
        } else if (journal.getPrivacyLevel()!=null && journal.getPrivacyLevel() == PrivacyLevel.SHARED) {
            hasAccess = journal.getAuthorEmail().equalsIgnoreCase(currentUserEmail) ||
                        journal.getSharedWithUserEmail().contains(currentUserEmail);
        } else if (journal.getPrivacyLevel()!=null && journal.getPrivacyLevel()==PrivacyLevel.PUBLIC) {
            hasAccess=true;
        }

        if(!hasAccess){
            throw new AccessDeniedException("You don't have permission to access this journal entry");
        }
    }

    private ObjectId validateAndConvertId(String idStr) {
        try{
            return new ObjectId(idStr);
        }catch (IllegalArgumentException e){
            throw new BadRequestException("Invalid journal id format: "+idStr);
        }
    }


    public List<JournalEntryDTO> getAllUserJournals(
            Boolean isShared,
            Boolean isFavourite,
            Boolean isMyCreated,
            String category,
            String privacyLevel,
            String sortBy,
            String userName)
    {

        try {
            log.info("fetch all user's journal...");
            User user = userRepository.findByUserName(userName);
            if(user==null){
                throw new ResourceNotFoundException("User not found: "+userName);
            }

            HashSet<ObjectId> finalJournalIds = collectJournalIds(isShared, isFavourite, isMyCreated, user);
            if(finalJournalIds.isEmpty())
                return Collections.emptyList();

            List<JournalEntries> journals=journalEntityRepository.findByIdIn(new ArrayList<>(finalJournalIds));

            // Filter
            List<JournalEntries> filteredJournals=journals.stream()
                    .filter(journal->(StringUtils.isBlank(category) || JournalCategory.valueOf(category.trim().toUpperCase()).equals(journal.getCategory())))
                    .filter(journal->(StringUtils.isBlank(privacyLevel) || PrivacyLevel.valueOf(privacyLevel.trim().toUpperCase()).equals(journal.getPrivacyLevel())))
                    .collect(Collectors.toList());

            // Sorting
            if(!StringUtils.isBlank(sortBy)){
                sortBy=sortBy.trim().toUpperCase();
                if(JournalSortBy.valueOf(sortBy)==JournalSortBy.LIKE)
                    filteredJournals.sort((i1, i2)->i2.getLikeCount().compareTo(i1.getLikeCount()));
                else if(JournalSortBy.valueOf(sortBy)==JournalSortBy.COMMENT)
                    filteredJournals.sort((i1,i2)->i2.getCommentCount().compareTo(i1.getCommentCount()));
                else if(JournalSortBy.valueOf(sortBy)==JournalSortBy.VIEW)
                    filteredJournals.sort((i1,i2)->i2.getViewCount().compareTo(i1.getViewCount()));
            }

            List<JournalEntryDTO> journalEntryDTOList = new ArrayList<>();
            filteredJournals.forEach(entry->journalEntryDTOList.add(journalEntityMapper.toDTO(entry)));
            return journalEntryDTOList;

        }catch (Exception e){
            throw new InternalServerErrorException("Some exception occurred, while fetching journals of user: "+userName+" Exception: "+e.getMessage());
        }

    }

    private HashSet<ObjectId> collectJournalIds(Boolean isShared, Boolean isFavourite, Boolean isMyCreated, User user) {

        HashSet<ObjectId> finalJournalIds = new HashSet<>();
        boolean anyFlagTrue = Boolean.TRUE.equals(isShared) ||
                Boolean.TRUE.equals(isFavourite) ||
                Boolean.TRUE.equals(isMyCreated);

        if(!anyFlagTrue){
            // no flag is active fetch all 3 types
            finalJournalIds.addAll(user.getSharedJournalIds());
            finalJournalIds.addAll(user.getFavoriteJournalIds());
            finalJournalIds.addAll(user.getJournalEntities().stream()
                    .map(JournalEntries::getId)
                    .collect(Collectors.toList()));
        }else{
            if(Boolean.TRUE.equals(isShared)){
                finalJournalIds.addAll(user.getSharedJournalIds());
            }

            if(Boolean.TRUE.equals(isFavourite)){
                finalJournalIds.addAll(user.getFavoriteJournalIds());
            }

            if(Boolean.TRUE.equals(isMyCreated)){
                finalJournalIds.addAll(user.getJournalEntities().stream()
                        .map(JournalEntries::getId)
                        .collect(Collectors.toList()));
            }
        }

        return finalJournalIds;
    }
}


// controller --> service --> repository