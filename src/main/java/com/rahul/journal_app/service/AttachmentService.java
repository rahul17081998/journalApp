package com.rahul.journal_app.service;

import com.rahul.journal_app.entity.Attachment;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file) throws Exception;

    Attachment getAttachment(ObjectId fileId) throws Exception;
}
