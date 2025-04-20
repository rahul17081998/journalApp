package com.rahul.journal_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {
    private String degree;
    private String institution;
    private String year;
    private Double marksPercentage;
} 