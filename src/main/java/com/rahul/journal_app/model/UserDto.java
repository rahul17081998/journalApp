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
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<JournalEntries> journalEntities= new ArrayList<>();
    private LocalDateTime userCreatedDate;
    private LocalDateTime userUpdatedDate;
}
