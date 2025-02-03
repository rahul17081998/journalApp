package com.rahul.journal_app.controller;

import com.rahul.journal_app.cache.AppCache;
import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.entity.User;
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

    @PostMapping("/grant-admin-access")
    public ResponseEntity<?> grantAdminAccessToUser(@RequestParam("email") String userName){
        if(!util.isValidEmail(userName)){
            return new ResponseEntity<>(Constants.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByUserName(userName);
        if(user==null){
            return new ResponseEntity<>(Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        if(util.isAdmin(userName)){
            return new ResponseEntity<>(Constants.USER_ALREADY_HAS_ADMIN_ACCESS, HttpStatus.BAD_REQUEST);
        }

        try{
            ResponseEntity<?> response = userService.updateRoleOfUser(user, true);
            if(response.getBody()!=null){
                return response;
            }
        }catch (Exception e){
            log.error("Exception, while granting admin access to the user {}", e.getMessage() ,e);
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(Constants.ADMIN_ACCESS_GRANT_EXCEPTION, HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/remove-admin-access")
    public ResponseEntity<?> removeAdminAccess(@RequestParam("email") String userName){
        if(!util.isValidEmail(userName)){
            return new ResponseEntity<>(Constants.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByUserName(userName);
        if(user==null){
            return new ResponseEntity<>(Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        if(!util.isAdmin(userName)){
            return new ResponseEntity<>(Constants.USER_DOES_NOT_HAVE_ADMIN_ACCESS, HttpStatus.BAD_REQUEST);
        }

        try{
            ResponseEntity<?> response = userService.updateRoleOfUser(user, false);
            if(response.getBody()!=null){
                return response;
            }
        }catch (Exception e){
            log.error("Exception, while granting admin access to the user {}", e.getMessage() ,e);
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(Constants.ADMIN_ACCESS_REMOVE_EXCEPTION, HttpStatus.BAD_REQUEST);
    }


}
