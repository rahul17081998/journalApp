package com.rahul.journal_app.service;

import com.rahul.journal_app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

//    @Test
//    public void testFindByUserName(){
//        Assertions.assertNotNull(userRepository.findByUserName("ram"));
//    }

}
