package com.rahul.journal_app.controller;

import com.rahul.journal_app.api.response.WeatherResponse;
import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.model.UserDto;
import com.rahul.journal_app.service.UserService;
import com.rahul.journal_app.service.WeatherService;
import com.rahul.journal_app.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final WeatherService weatherService;

    @Autowired
    private Util util;

    public UserController(UserService userService, WeatherService weatherService) {
        this.userService = userService;
        this.weatherService = weatherService;
    }


    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody User user){
        logger.info("> User Update begin...");
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        try {
            ResponseEntity<?> response=userService.updateUser(username, user);
            return response;
        }catch (Exception e){
            logger.info("Exception during updating user information: {}", e.getMessage(), e);
        }
        return new ResponseEntity<>(Constants.USER_NOT_UPDATED, HttpStatus.BAD_REQUEST);

    }

    @DeleteMapping()
    public ResponseEntity<String> deleteUser(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        String result=userService.deleteUserByUsername(username);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping()
    public ResponseEntity<?> greeting(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        User user=userService.findByUserName(username);
        String city=user.getCity();
        String feelsLikeTemp="NA";
        String firstname = user.getUserName()==null?"User":util.capitalizeFirstChar(user.getFirstName());
        if(city ==null || city.equals("")){
            return new ResponseEntity<>("The city field for the user is either empty or null.", HttpStatus.NOT_FOUND);
        }
        WeatherResponse weatherResponse=weatherService.getWeather(city);
        if(weatherResponse!=null){
            if(weatherResponse.getCurrent()!=null){
                feelsLikeTemp= String.valueOf(weatherResponse.getCurrent().getFeelslike());
            }
        }
        return new ResponseEntity<>("Hello " + firstname + ", the weather in " + city + " feels like " + feelsLikeTemp + ".", HttpStatus.OK);
    }


    @GetMapping("/user-details")
    public ResponseEntity<?> getUserDetails(){
        try{
            Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
            String username= authentication.getName();
            UserDto user = userService.getUserDetail(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("User Not Found: ", HttpStatus.OK);
        }
    }
}
