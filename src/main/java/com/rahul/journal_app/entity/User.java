package com.rahul.journal_app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rahul.journal_app.serializer.ObjectIdToStringSerializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "users")
@AllArgsConstructor
public class User {

    @Id
    @JsonSerialize(using = ObjectIdToStringSerializer.class)
    private ObjectId id;

    @Indexed(unique = true)
    @NotBlank(message = "User cannot be blank")
    private String userName;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy") // Custom format
    private LocalDate dateOfBirth;
    private String phoneNo;
    private String country;
    private String gender;
    private String pinCode;
    private String city;
    private boolean sentimentAnalysis;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @DBRef
    private List<JournalEntries> journalEntities= new ArrayList<>();
    private List<String> roles;
    private boolean verified;
    @CreatedDate
    private LocalDateTime userCreatedDate;
    @LastModifiedDate
    private LocalDateTime UserUpdatedDate;
}
