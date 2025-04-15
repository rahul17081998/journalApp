package com.rahul.journal_app.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rahul.journal_app.serializer.ObjectIdToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String street;
    private String city;
    private String state;
    private String country;
    private String pinCode;
} 