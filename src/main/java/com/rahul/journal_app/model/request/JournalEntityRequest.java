package com.rahul.journal_app.model.request;

import com.rahul.journal_app.enums.Sentiment;
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
public class JournalEntityRequest {

    private String title;
    private String content;
    @CreatedDate
    private Date date;
    private String sentiment;
}
