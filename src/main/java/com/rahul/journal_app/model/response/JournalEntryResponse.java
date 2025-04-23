package com.rahul.journal_app.model.response;

import com.rahul.journal_app.enums.Sentiment;
import com.rahul.journal_app.model.Author;
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
public class JournalEntryResponse {

    private String title;
    private String content;
    private String sentiment;
    private Author author;
    private Date date;
}
