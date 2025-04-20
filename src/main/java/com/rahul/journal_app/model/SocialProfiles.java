package com.rahul.journal_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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