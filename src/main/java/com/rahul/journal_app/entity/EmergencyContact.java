package com.rahul.journal_app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document(collection = "emergency_contacts")
public class EmergencyContact {
    private String name;
    private String relation;
    private String phone;
} 