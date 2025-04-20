package com.rahul.journal_app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document(collection = "social_profiles")
public class SocialProfiles {
    private String linkedin;
    private String twitter;
    private String facebook;
    private String instagram;
    private String github;
    private String youtube;
    private String medium;
    private String stackoverflow;
    private String website;
    // Can be extended with other social media platforms as needed
} 