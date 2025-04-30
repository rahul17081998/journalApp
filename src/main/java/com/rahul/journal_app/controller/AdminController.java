package com.rahul.journal_app.controller;

import com.rahul.journal_app.cache.AppCache;
import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.model.ApiResponse;
import com.rahul.journal_app.model.UserDto;
import com.rahul.journal_app.repository.UserRepository;
import com.rahul.journal_app.service.UserService;
import com.rahul.journal_app.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @Autowired
    private Util util;

    @Autowired
    private UserRepository userRepository;



    @GetMapping("/clear-app-cache")
    public void clearAppCache(){
        appCache.init();
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        List<UserDto> allUsers =userService.getAllUsers();
        if(allUsers !=null && !allUsers.isEmpty()){
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add-admin")
    public ResponseEntity<?> addAdmin(@RequestBody User user){
        if(!util.isValidEmail(user.getUserName())){
            return new ResponseEntity<>(Constants.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST);
        }
        User dbuser=userRepository.findByUserName(user.getUserName());
        if(dbuser!=null){
            return new ResponseEntity<>(Constants.USER_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        }
        Boolean isUserAdded=userService.addAdmin(user);
        if(isUserAdded!=null && isUserAdded){
            return new ResponseEntity<>(user.getUserName()+" added with Admin Access", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("An error occur to add the user", HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/update-user-role")
    public ResponseEntity<ApiResponse<?>> updateUserRole(@RequestParam("email") String userName, @RequestParam("grantAdmin") boolean grantAdmin){
        log.info("Received request to {} admin access for user: {}", grantAdmin?"grant":"remove", userName);

        if(!util.isValidEmail(userName)){
            log.warn("Invalid email format received: {}", userName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(ErrorCode.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST));
        }

        try{
            return userService.updateRoleOfUser(userName, grantAdmin);
        }catch (Exception e){
            log.error("Unexpected exception while updating user role for {}: {}", userName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.USER_REGISTRATION_FAILED, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));

        }
    }

}
