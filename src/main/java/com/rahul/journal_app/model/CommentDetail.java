package com.rahul.journal_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDetail {

    private String commenterEmailId;        // Username of commenter
    private String commenterName;      // Full name of commenter
    private String content;
    @CreatedDate
    private Date createdDate;
    private boolean isOriginalAuthorReply;  // True if author's own reflection

}
