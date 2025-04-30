package com.rahul.journal_app.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rahul.journal_app.enums.JournalCategory;
import com.rahul.journal_app.enums.PrivacyLevel;
import com.rahul.journal_app.enums.Sentiment;
import com.rahul.journal_app.model.CommentDetail;
import com.rahul.journal_app.model.LikedByUser;
import com.rahul.journal_app.serializer.ObjectIdToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "journal_entries")
@Builder
public class JournalEntries {

    @Id
    @JsonSerialize(using = ObjectIdToStringSerializer.class)
    private ObjectId id;
    private String title;
    private String content;
    private Sentiment sentiment;
    private String authorEmail;        // Username/email of author
    private String authorName;      // Full name of author

    @CreatedDate
    private Date date;


    @Builder.Default
    private JournalCategory category=JournalCategory.DAILY;   // DAILY", "GRATITUDE", "REFLECTION", "GOALS", "DREAM", "TRAVEL", "HEALTH", "WORK", etc.

    // Emotional data
    private String mood;

    @Builder.Default
    private Integer emotionRating = 1;  // 1-10 scale

    @LastModifiedDate
    private Date lastModifiedDate;

    @Builder.Default
    private List<String> attachmentIds=new ArrayList<>();

    private Integer wordCount;

    @Builder.Default
    private Integer viewCount=0;

    // Sharing & Privacy
    @Builder.Default
    private PrivacyLevel privacyLevel = PrivacyLevel.PUBLIC;  // PRIVATE, SHARED, PUBLIC

    @Builder.Default
    private HashSet<String> sharedWithUserEmail=new HashSet<>();

    // Engagement
    @Builder.Default
    private List<CommentDetail> comments=new ArrayList<>();

//    @Builder.Default
//    private Integer commentCount = 0;
//
//    @Builder.Default
//    private Integer likeCount = 0;

    @Builder.Default
    private HashSet<String> likedByUsers=new HashSet<>();  // Usernames who liked the entry

}
