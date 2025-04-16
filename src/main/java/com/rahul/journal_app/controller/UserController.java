package com.rahul.journal_app.controller;

import com.rahul.journal_app.api.response.WeatherResponse;
import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.model.ApiResponse;
import com.rahul.journal_app.model.GreetingResponse;
import com.rahul.journal_app.model.UserDto;
import com.rahul.journal_app.service.AttachmentService;
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
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final WeatherService weatherService;

    private final AttachmentService attachmentService;

    @Autowired
    private Util util;

    public UserController(UserService userService, AttachmentController attachmentController, WeatherService weatherService, AttachmentService attachmentService) {
        this.userService = userService;
        this.weatherService = weatherService;
        this.attachmentService = attachmentService;
    }


    @PutMapping()
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@RequestBody User user) throws Exception {
        logger.info("> User Update begin...");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDtoResponse = userService.updateUser(username, user);
        return ResponseEntity.ok(ApiResponse.success(userDtoResponse, Constants.USER_DETAILS_UPDATED_SUCCESSFULLY));
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteUser(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        String result=userService.deleteUserByUsername(username);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping()
    public ResponseEntity<ApiResponse<WeatherResponse>> greeting(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        String city="";
        try{
            User user=userService.findByUserName(username);
            city=user.getCity();
            String firstname = user.getUserName()==null?"User":util.capitalizeFirstChar(user.getFirstName());
            if(city == null || city.trim().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(ErrorCode.CITY_FIELD_EMPTY_OR_NULL, null, HttpStatus.NOT_FOUND));
            }
            WeatherResponse weatherResponse=weatherService.getWeather(city);
            if (weatherResponse != null && weatherResponse.getCurrent() != null) {
                String feelsLikeTemp = String.valueOf(weatherResponse.getCurrent().getFeelslike());
                GreetingResponse greetingResponse = new GreetingResponse("Hello " + firstname + ", the weather in " + city + " feels like " + feelsLikeTemp);
                return ResponseEntity.ok(ApiResponse.success(weatherResponse, Constants.WEATHER_FETCH_SUCCESSFULLY_MSG));
            }
            // If weatherResponse or current is null
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.WEATHER_REPORT_FETCH_FAILED, "Incomplete weather data", HttpStatus.INTERNAL_SERVER_ERROR));

        }catch (Exception e){
            logger.info("Failed to fetch weather report for the provided city {}", city);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.WEATHER_REPORT_FETCH_FAILED, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));

        }
    }


    @GetMapping("/user-details")
    public ResponseEntity<ApiResponse<UserDto>> getUserDetails(){
        try{
            Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
            String username= authentication.getName();
            UserDto userDto = userService.getUserDetail(username);
            return ResponseEntity.ok(ApiResponse.success(userDto, Constants.USER_DETAILS_FETCHED_SUCCESSFULLY_MSG));
        }catch (Exception e){
            HttpStatus status = HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status)
                    .body(ApiResponse.error(ErrorCode.USER_NOT_FOUND, e.getMessage(), status));
        }
    }

    @PostMapping("/upload-profile-photo")
    public ResponseEntity<ApiResponse<UserDto>> uploadProfilePhoto(@RequestParam("file") MultipartFile file) throws Exception {
        logger.info("Profile picture updating... ");
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        try {
            UserDto userDtoResponse = userService.updateUserProfilePhoto(username, file);
            return ResponseEntity.ok(ApiResponse.success(userDtoResponse, Constants.USER_PROFILE_PICTURE_UPDATED_SUCCESSFULLY_MSG));
        }catch (Exception e){
            logger.info("Exception during updating user information: {}", e.getMessage(), e);
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status)
                    .body(ApiResponse.error(ErrorCode.PROFILE_PHOTO_UPDATE_FAILED, e.getMessage(), status));}
    }
}
