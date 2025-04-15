package com.rahul.journal_app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document(collection = "preferences")
public class Preferences {
    private String theme;
    private String timezone;
    private boolean notificationEnabled;
    private boolean emailNotifications;
    private boolean smsNotifications;
    private String language;
    private String dateFormat;
    private String timeFormat;
    private boolean darkMode;
} 