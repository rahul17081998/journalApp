package com.rahul.journalApp.repository;

import com.rahul.journalApp.entity.JournalEntries;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntityRepository extends MongoRepository<JournalEntries, ObjectId> {
}
