package com.rahul.journal_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryDTO {
    private String id;
    private String title;
    private String content;
    private String sentiment;
    private String authorEmail;
    private String authorName;
    private Date createdDate;
    private Date lastModifiedDate;
    private String category;
    private String mood;
    private Integer emotionRating;
    private Integer viewCount;
    private String privacyLevel;
    private HashSet<String> sharedWithUserEmails;
    private List<CommentDetail> comments;
    private Integer commentCount;
    private Integer likeCount;
    private boolean likedByCurrentUser;
}
