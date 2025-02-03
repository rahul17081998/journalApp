package com.rahul.journal_app.repository;

import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.entity.UserOtp;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpRepository extends MongoRepository<UserOtp, ObjectId> {

    Optional<UserOtp> findByUserName(String userName);

    void deleteByUserName(String username);

    //Optional<UserOtp> findByEmail(String email);
}
