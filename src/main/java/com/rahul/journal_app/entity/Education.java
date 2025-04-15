package com.rahul.journal_app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document(collection = "education")
public class Education {
    private String degree;
    private String institution;
    private String year;
    private Double marksPercentage;
} 