package com.rahul.journal_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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