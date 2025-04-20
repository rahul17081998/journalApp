package com.rahul.journal_app.service;

import com.rahul.journal_app.entity.Attachment;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file) throws Exception;

    Attachment getAttachment(ObjectId fileId) throws Exception;

    void deleteAttachment(String profileImageUrl) throws FileNotFoundException;
}
