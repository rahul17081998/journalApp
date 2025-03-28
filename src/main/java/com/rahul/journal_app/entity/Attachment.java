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
@Document(collection = "attachment")
public class Attachment {

    @Id
    @JsonSerialize(using = ObjectIdToStringSerializer.class)
    private ObjectId id;
    private String fileName;
    private String fileType;

    private byte[] data;

    public Attachment(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}
