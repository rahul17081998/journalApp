package com.rahul.journal_app.controller;

import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.entity.Attachment;
import com.rahul.journal_app.model.ApiResponse;
import com.rahul.journal_app.model.response.LoginResponse;
import com.rahul.journal_app.model.response.TwitterUser;
import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.repository.UserRepository;
import com.rahul.journal_app.model.request.PasswordRestRequest;
import com.rahul.journal_app.service.*;
import com.rahul.journal_app.utils.JwtUtil;
import com.rahul.journal_app.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

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
    private final AttachmentService attachmentService;
    @Autowired
    private SmsService smsService;

    @Value("${media.velinq.logo}")
    private String velinqLogo;

    public PublicController(UserService userService, TwitterService twitterService, AttachmentService attachmentService) {
        this.userService = userService;
        this.twitterService = twitterService;
        this.attachmentService = attachmentService;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.INVALID_EMAIL_FORMAT, null, HttpStatus.BAD_REQUEST));
        }

        User dbuser=userRepository.findByUserName(user.getUserName());
        if(dbuser!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.USER_ALREADY_EXIST, null, HttpStatus.BAD_REQUEST));
        }

        if(user.getPassword()==null || user.getPassword().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.PASSWORD_CAN_NOT_BE_NULL_OR_EMPTY, null, HttpStatus.BAD_REQUEST));
        }

        try {
             userService.saveNewUser(user);
        }catch (Exception e){
            log.error("Exception occurred during signup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.USER_REGISTRATION_FAILED, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }

        Map<String, String> response = Map.of(
                "email", user.getUserName(),
                "message", Constants.USER_VERIFICATION_EMAIL_SENT_SUCCESSFULLY
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .success(response, Constants.SIGNUP_SUCCESSFUL_MSG, HttpStatus.CREATED));
    }

    // create jwt token
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody User user){
        log.info("Start login");
        if(user.getUserName() !=null && !user.getUserName().trim().isEmpty()){
            user.setUserName(user.getUserName().toLowerCase());
        }

        User dbUser=userRepository.findByUserName(user.getUserName());
        if(dbUser==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.USER_NOT_FOUND, null, HttpStatus.BAD_REQUEST));
        }

        try {

            try {
                // try to check user authentication
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
                );
                log.info("User authenticated: {}", user.getUserName());
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(ErrorCode.INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST));
            }


            UserDetails userDetails =userDetailsServiceImpl.loadUserByUsername(user.getUserName());
            if(!userDetails.isEnabled()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(ErrorCode.USER_NOT_VERIFIED, null, HttpStatus.BAD_REQUEST));            }
            LoginResponse loginResponse = jwtUtil.getLoginResponse(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(loginResponse, Constants.LOGIN_SUCCESSFUL_MSG));

        }catch (Exception e){
            log.error("Exception occurred while authenticating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.FAILED_TO_CREATE_JWT_TOKEN, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/send-forget-password-otp")
    public ResponseEntity<ApiResponse<?>> getForgetPasswordEmailOtp(@RequestParam("email") String email){
        if(!util.isValidEmail(email)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.INVALID_EMAIL_FORMAT, null, HttpStatus.BAD_REQUEST));
        }

        User user=userService.findUserByEmail(email);
        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ErrorCode.EMAIL_NOT_FOUND, null, HttpStatus.NOT_FOUND));
        }

        try{
            userService.sendForgetPasswordEmailOtp(user);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(ErrorCode.EMAIL_SENDING_FAILED, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
        return ResponseEntity.ok(ApiResponse.success(Constants.EMAIL_SENT_SUCCESSFULLY_MSG));
    }


    @GetMapping("/verify-user")
    public ResponseEntity<ApiResponse<?>> verifyUser(@RequestParam("userName") String userName,
                                        @RequestParam("otp") String otp){
        log.info("User email verification initiated for: {}", userName);
        try {
            ResponseEntity<ApiResponse<?>> response=userService.verifyUser(userName, otp);
            return response;
        }catch (Exception e){
            log.error("Exception during user verification for {}: {}", userName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.FAILED_TO_VERIFY_USER_EMAIL,e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody PasswordRestRequest passwordRestRequest){
        return  userService.resetPassword(passwordRestRequest);
    }

    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSMS(@RequestParam("phoneNo") String phoneNo){
        try{
            return smsService.sendWelcomeSMST(phoneNo);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(ErrorCode.FAILED_TO_SEND_MSG, HttpStatus.BAD_REQUEST));
        }
    }


    @GetMapping("/logo")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile() throws Exception {
        ObjectId logoId = new ObjectId(velinqLogo); // your file ID

        Attachment attachment = attachmentService.getAttachment(logoId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }
}
