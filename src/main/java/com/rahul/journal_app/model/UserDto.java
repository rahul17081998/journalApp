package com.rahul.journal_app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.serializer.ObjectIdToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @JsonSerialize(using = ObjectIdToStringSerializer.class)
    private ObjectId id;
    private String userName;
    private String firstName;
    private String lastName;
    private String gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")  // Custom format
    private LocalDate dateOfBirth;
    private String phoneNo;
    private String city;
    private String pinCode;
    private String country;
    private boolean sentimentAnalysis;
    private boolean verified;
    private List<String> roles;
    private LocalDateTime userCreatedDate;
    private LocalDateTime userUpdatedDate;
    
    // New fields
    private String profileImageUrl;
    private String maritalStatus;
    private String occupation;
    private String company;
    private List<Education> education;
    private String alternatePhone;
    private String email;
    private String alternateEmail;
    private Address address;
    private SocialProfiles socialProfiles;
    private List<String> languages;
    private List<String> skills;
    private List<String> interests;
    private EmergencyContact emergencyContact;
    private String bloodGroup;
    private Preferences preferences;
    private Set<String> sharedJournalIds = new HashSet<>();
    private Set<String> favoriteJournalIds=new HashSet<>();  // Favorite journal entries
    private List<JournalEntries> journalEntities= new ArrayList<>();

}
