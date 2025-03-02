package com.rahul.journal_app.repository;

import com.rahul.journal_app.entity.Attachment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends MongoRepository<Attachment, ObjectId> {
//    Attachment findById(String fileId);
}
