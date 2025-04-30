package com.rahul.journal_app.mapper;

import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.model.JournalEntryDTO;
import org.springframework.stereotype.Component;

@Component
public class JournalEntityMapper {

    public JournalEntryDTO toDTO(JournalEntries entity){

        return JournalEntryDTO.builder()
                .id(entity.getId().toString())
                .title(entity.getTitle())
                .content(entity.getContent())
                .sentiment(entity.getSentiment()==null? null: entity.getSentiment().name())
                .authorEmail(entity.getAuthorEmail())
                .authorName(entity.getAuthorName())
                .createdDate(entity.getDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .category(entity.getCategory()==null? null:entity.getCategory().name())
                .mood(entity.getMood())
                .emotionRating(entity.getEmotionRating())
                .viewCount(entity.getViewCount())
                .privacyLevel(entity.getPrivacyLevel().name())
                .sharedWithUserEmails(entity.getSharedWithUserEmail())
                .commentCount(entity.getComments().size())
                .likeCount(entity.getLikedByUsers().size())

                .build();
    }

    public JournalEntryDTO toDTO(String userName, JournalEntries entity){

        return JournalEntryDTO.builder()
                .id(entity.getId().toString())
                .title(entity.getTitle())
                .content(entity.getContent())
                .sentiment(entity.getSentiment()==null? null: entity.getSentiment().name())
                .authorEmail(entity.getAuthorEmail())
                .authorName(entity.getAuthorName())
                .createdDate(entity.getDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .category(entity.getCategory()==null? null:entity.getCategory().name())
                .mood(entity.getMood())
                .emotionRating(entity.getEmotionRating())
                .viewCount(entity.getViewCount())
                .privacyLevel(entity.getPrivacyLevel().name())
                .sharedWithUserEmails(entity.getSharedWithUserEmail())
                .comments(entity.getComments())
                .likedByUsers(entity.getLikedByUsers())
                .commentCount(entity.getComments().size())
                .likeCount(entity.getLikedByUsers().size())
                .currentUserLiked(entity.getLikedByUsers().contains(userName))
                .build();
    }


}
