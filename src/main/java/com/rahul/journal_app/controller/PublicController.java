package com.rahul.journal_app.controller;

import com.rahul.journal_app.api.response.TwitterUser;
import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.model.UserOtpDto;
import com.rahul.journal_app.repository.UserRepository;
import com.rahul.journal_app.request.PasswordRestRequest;
import com.rahul.journal_app.service.SmsService;
import com.rahul.journal_app.service.TwitterService;
import com.rahul.journal_app.service.UserDetailsServiceImpl;
import com.rahul.journal_app.service.UserService;
import com.rahul.journal_app.utils.JwtUtil;
import com.rahul.journal_app.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Util util;

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;
    private final TwitterService twitterService;
    @Autowired
    private SmsService smsService;

    public PublicController(UserService userService, TwitterService twitterService) {
        this.userService = userService;
        this.twitterService = twitterService;
    }


    @GetMapping("/health-check")
    public String healthCheck(){
        log.info("> Trigger health-check API");
        return "ok";
    }

    @GetMapping("/user-tweets")
    public ResponseEntity<?> getUserTwitterTweets(@RequestParam("id") String id){
        TwitterUser twitterUser = twitterService.getTweet(id);
        if(twitterUser!=null){
            return new ResponseEntity<>(twitterUser.getDisplayText(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // create user
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user){
        user.setRoles(Arrays.asList("USER"));
        if(!util.isValidEmail(user.getUserName())){
            return new ResponseEntity<>(Constants.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST);
        }
        User dbuser=userRepository.findByUserName(user.getUserName());
        if(dbuser!=null){
            return new ResponseEntity<>(Constants.USER_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        }
        try {
            userService.saveNewUser(user);
        }catch (Exception e){
            log.info("Exception: {}",e.getMessage());
            return new ResponseEntity<>(Constants.EXCEPTION_OCCURRED_DURING_USER_REGISTRATION, HttpStatus.OK);
        }
        return new ResponseEntity<>(Constants.USER_VERIFICATION_EMAIL_SENT_SUCCESSFULLY+user.getUserName(), HttpStatus.OK);
    }

    // create jwt token
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
        log.info("Start login");
        if(user.getUserName() !=null && !user.getUserName().equals("")){
            user.setUserName(user.getUserName().toLowerCase());
        }

        User dbUser=userRepository.findByUserName(user.getUserName());
        if(dbUser==null){
            return new ResponseEntity<>(Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        try {
            // try to check user authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );
            log.info("User authenticated: {}", user.getUserName());


            UserDetails userDetails =userDetailsServiceImpl.loadUserByUsername(user.getUserName());
            if(!userDetails.isEnabled()){
                return new ResponseEntity<>(Constants.USER_NOT_VERIFIED, HttpStatus.BAD_REQUEST);
            }
            String jwt=jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);

        }catch (Exception e){
            log.error("Exception occurred while creatingAuthenticationToken: {}", e.getMessage());
            return new ResponseEntity<>(Constants.INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/send-forget-password-otp")
    public ResponseEntity<String> getForgetPasswordEmailOtp(@RequestParam("email") String email){
        if(!util.isValidEmail(email)){
            return new ResponseEntity<>(Constants.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST);
        }
        User user=userService.findUserByEmail(email);
        if(user !=null){
            userService.sendForgetPasswordEmailOtp(user);
            return new ResponseEntity<>(Constants.EMAIL_SUCCESSFULLY_SENT, HttpStatus.OK);
        }
        return new ResponseEntity<>(Constants.EMAIL_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/verify-user")
    public ResponseEntity<?> verifyUser(@RequestParam("userName") String userName,
                                        @RequestParam("otp") String otp){
        log.info("User email verification begin");
        try {
            ResponseEntity<?> response=userService.verifyUser(userName, otp);
            return response;
        }catch (Exception e){
            return new ResponseEntity<>(Constants.EXCEPTION_OCCURRED_DURING_USER_VERIFICATION, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordRestRequest passwordRestRequest){
        ResponseEntity<?> response=null;
        try{
            response = userService.resetPassword(passwordRestRequest);
        }catch (Exception e){
            throw new RuntimeException(Constants.PASSWORD_RESET_EXCEPTION_OCCURRED);
        }
        return response;
    }

    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSMS(@RequestParam("phoneNo") String phoneNo){
        if (!phoneNo.startsWith("+")) {
            phoneNo= "+" + phoneNo;
        }
        try{
            ResponseEntity<?> response=smsService.sendWelcomeSMST(phoneNo);
            return response;
        }catch (Exception e){
            return new ResponseEntity<>("Exception while sending sms to user", HttpStatus.BAD_REQUEST);
        }
    }

}
