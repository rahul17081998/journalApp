package com.rahul.journal_app.service;

import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.entity.Attachment;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.entity.UserOtp;
import com.rahul.journal_app.enums.Gender;
import com.rahul.journal_app.enums.RoleEnum;
import com.rahul.journal_app.exception.BadOtpException;
import com.rahul.journal_app.exception.BadRequestException;
import com.rahul.journal_app.exception.InternalServerErrorException;
import com.rahul.journal_app.model.ApiResponse;
import com.rahul.journal_app.model.UserDto;
import com.rahul.journal_app.repository.JournalEntityRepository;
import com.rahul.journal_app.repository.UserOtpRepository;
import com.rahul.journal_app.repository.UserRepository;
import com.rahul.journal_app.model.request.PasswordRestRequest;
import com.rahul.journal_app.utils.Util;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
@Slf4j
public class UserService {


    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private UserOtpRepository userOtpRepository;

    @Autowired
    private Util util;


    @Autowired
    private JournalEntityRepository journalEntityRepository;

    private static final SecureRandom random = new SecureRandom();

    @Value("${otp.expiration_time}")
    private long otpExpirationTimeInMinute;




    @Transactional
    public void saveNewUser(User user){
        user.setUserName(user.getUserName().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getUserCreatedDate() == null) {
            user.setUserCreatedDate(LocalDateTime.now());
        }
        if(StringUtils.isBlank(user.getEmail())){
            user.setEmail(user.getUserName());
        }
        user.setUserUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        UserOtp userOtp = new UserOtp();
        userOtp.setUserName(user.getUserName());
        userOtp.setOtp(generateOtp());
        userOtp.setOtpCreatedDateTime(LocalDateTime.now());

        UserOtp userOtpSaved = userOtpRepository.save(userOtp);
        sendOtpVerificationEmail(user.getFirstName(), userOtpSaved.getUserName(), userOtpSaved.getOtp());
        log.info("User Successfully Registered: {}", user.getUserName());
    }

    private void sendOtpVerificationEmail(String firstName, String userName, String otp) {
        String subject = "Verify Your Email Address for JournalApp Registration";
        String body = util.getBodyForOtpVerificationMail(firstName, userName, otp);
        emailService.sendEmailWithEmbeddedLogo(userName, subject, body);
        log.info("Sent verification email to {}", userName);
    }

    private String generateOtp() {
        int otp = 100000+random.nextInt(900000); // generate 6-digit number
        return String.valueOf(otp);
    }


    public UserDto updateUser(String username, User user) throws Exception {

// Validation
        if (user.getPhoneNo() != null && !util.isValidPhoneNumber(user.getPhoneNo())) {
            throw new BadRequestException(ErrorCode.INVALID_PHONE_NUMBER);
        }

        if (user.getDateOfBirth() != null && !util.isValidDateOfBirth(user.getDateOfBirth().toString(), "yyyy-MM-dd")) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_OF_BIRTH);
        }

        if (user.getGender() != null &&
                !user.getGender().equalsIgnoreCase(Gender.MALE.toString()) &&
                !user.getGender().equalsIgnoreCase(Gender.FEMALE.toString())) {
            throw new BadRequestException(ErrorCode.INVALID_GENDER);
        }

        try {
            User savedUserInfo = findByUserName(username);
            if (savedUserInfo == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            savedUserInfo.setFirstName((user.getFirstName() != null && !user.getFirstName().equals("")) ? user.getFirstName() : savedUserInfo.getFirstName());
            savedUserInfo.setLastName((user.getLastName() != null && !user.getLastName().equals("")) ? user.getLastName() : savedUserInfo.getLastName());
            savedUserInfo.setCity((user.getCity() != null && !user.getCity().equals("")) ? user.getCity() : savedUserInfo.getCity());
            savedUserInfo.setCountry(user.getCountry()!=null? user.getCountry():savedUserInfo.getCountry());
            savedUserInfo.setPhoneNo(user.getPhoneNo()!=null? user.getPhoneNo():savedUserInfo.getPhoneNo());
            savedUserInfo.setPinCode(user.getPinCode()!=null? user.getPinCode():savedUserInfo.getPinCode());
            savedUserInfo.setGender(user.getGender()!=null? user.getGender().toUpperCase():savedUserInfo.getGender());
            savedUserInfo.setDateOfBirth(user.getDateOfBirth()!=null? user.getDateOfBirth():savedUserInfo.getDateOfBirth());
            savedUserInfo.setAlternateEmail(user.getAlternateEmail()!=null?user.getAlternateEmail(): savedUserInfo.getAlternateEmail());
            savedUserInfo.setAlternatePhone(user.getAlternatePhone()!=null?user.getAlternatePhone(): savedUserInfo.getAlternateEmail());
            savedUserInfo.setCompany(user.getCompany()!=null?user.getCompany():savedUserInfo.getCompany());
            savedUserInfo.setAddress(user.getAddress()!=null?user.getAddress():savedUserInfo.getAddress());
            savedUserInfo.setBloodGroup(user.getBloodGroup()!=null?user.getBloodGroup():savedUserInfo.getBloodGroup());
            savedUserInfo.setEmergencyContact(user.getEmergencyContact()!=null?user.getEmergencyContact():savedUserInfo.getEmergencyContact());
            savedUserInfo.setLanguages(user.getLanguages()!=null?user.getLanguages():savedUserInfo.getLanguages());
            savedUserInfo.setMaritalStatus(user.getMaritalStatus()!=null?user.getMaritalStatus():savedUserInfo.getMaritalStatus());
            savedUserInfo.setPreferences(user.getPreferences()!=null?user.getPreferences():savedUserInfo.getPreferences());
            savedUserInfo.setSkills(user.getSkills()!=null?user.getSkills():savedUserInfo.getSkills());
            savedUserInfo.setSocialProfiles(user.getSocialProfiles()!=null?user.getSocialProfiles():savedUserInfo.getSocialProfiles());
            savedUserInfo.setEmail(user.getUserName()!=null?user.getUserName():savedUserInfo.getUserName());
            savedUserInfo.setOccupation(user.getOccupation()!=null?user.getOccupation():savedUserInfo.getOccupation());
            savedUserInfo.setInterests(user.getInterests()!=null?user.getInterests():savedUserInfo.getInterests());
            savedUserInfo.setEducation(user.getEducation()!=null?user.getEducation():savedUserInfo.getEducation());


            User updatedUser = userRepository.save(savedUserInfo);
            return convertUserToUserDto(updatedUser);
        }catch (Exception e){
            log.error("Error while updating user info in database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user information", e);
        }
    }

//    public User saveUserEntry(User user){
//        User savedUser = userRepository.save(user);
//        return savedUser;
//    }

    public List<UserDto> getAllUsers(){
        List<User> allUser= userRepository.findAll();
        List<UserDto> allUserDtoResponse= new ArrayList<>();
        for(User user: allUser){
            UserDto userDtoResponse = convertUserToUserDto(user);
            allUserDtoResponse.add(userDtoResponse);
        }
        return allUserDtoResponse;
    }

    private UserDto convertUserToUserDto(User user){

        List<com.rahul.journal_app.model.Education> educationDtoList=new ArrayList<>();
        if(user.getEducation()!=null && !user.getEducation().isEmpty()){
            educationDtoList= user.getEducation()
                    .stream()
                    .map(this::convertEducationEntityToDto)
                    .collect(Collectors.toList());
        }

        String profileImageUrlDownload = "";
        if(user.getProfileImageUrl()!=null && !user.getProfileImageUrl().isEmpty()){
            profileImageUrlDownload= ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/attachment/download/")
                    .path(user.getProfileImageUrl())
                    .toUriString();
        }


        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNo(user.getPhoneNo())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .country(user.getCountry())
                .city(user.getCity())
                .pinCode(user.getPinCode())
                .journalEntities(user.getJournalEntities())
                .roles(user.getRoles())
                .verified(user.isVerified())
                .sentimentAnalysis(user.isSentimentAnalysis())
                .userCreatedDate(user.getUserCreatedDate())
                .userUpdatedDate(user.getUserUpdatedDate())
                .profileImageUrl(profileImageUrlDownload.isEmpty()?user.getProfileImageUrl():profileImageUrlDownload)
                .maritalStatus(user.getMaritalStatus())
                .occupation(user.getOccupation())
                .company(user.getCompany())
                .education(educationDtoList)
                .alternatePhone(user.getAlternatePhone())
                .email(user.getEmail())
                .alternateEmail(user.getAlternateEmail())
                .address(convertAddressEntityToDto(user.getAddress()))
                .socialProfiles(convertSocialProfilesEntityToDto(user.getSocialProfiles()))
                .languages(user.getLanguages())
                .skills(user.getSkills())
                .interests(user.getInterests())
                .emergencyContact(convertEmergencyContactToDto(user.getEmergencyContact()))
                .bloodGroup(user.getBloodGroup())
                .preferences(convertPreferencesToDto(user.getPreferences()))
                .build();
        return userDto;
    }

    private com.rahul.journal_app.model.Education convertEducationEntityToDto(com.rahul.journal_app.entity.Education entity) {
        if(entity==null) return null;
        return com.rahul.journal_app.model.Education.builder()
                .degree(entity.getDegree())
                .institution(entity.getInstitution())
                .year(entity.getYear())
                .marksPercentage(entity.getMarksPercentage())
                .build();
    }

    private com.rahul.journal_app.model.Address convertAddressEntityToDto(com.rahul.journal_app.entity.Address entity){
        if(entity==null) return null;
        return com.rahul.journal_app.model.Address.builder()
                .street(entity.getStreet())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .pinCode(entity.getPinCode())
                .build();

    }

    private com.rahul.journal_app.model.SocialProfiles convertSocialProfilesEntityToDto(com.rahul.journal_app.entity.SocialProfiles entity){
        if(entity==null) return null;
        return com.rahul.journal_app.model.SocialProfiles.builder()
                .facebook(entity.getFacebook())
                .github(entity.getGithub())
                .instagram(entity.getInstagram())
                .linkedin(entity.getLinkedin())
                .medium(entity.getMedium())
                .stackoverflow(entity.getStackoverflow())
                .twitter(entity.getTwitter())
                .youtube(entity.getYoutube())
                .website(entity.getWebsite())
                .build();
    }

    private com.rahul.journal_app.model.EmergencyContact convertEmergencyContactToDto(com.rahul.journal_app.entity.EmergencyContact entity){
        if(entity==null) return null;
        return com.rahul.journal_app.model.EmergencyContact.builder()
                .name(entity.getName())
                .relation(entity.getRelation())
                .phone(entity.getPhone())
                .build();

    }

    private com.rahul.journal_app.model.Preferences convertPreferencesToDto(com.rahul.journal_app.entity.Preferences entity){
        if(entity==null) return null;
        return com.rahul.journal_app.model.Preferences.builder()
                .theme(entity.getTheme())
                .darkMode(entity.isDarkMode())
                .dateFormat(entity.getDateFormat())
                .emailNotifications(entity.isEmailNotifications())
                .notificationEnabled(entity.isNotificationEnabled())
                .smsNotifications(entity.isSmsNotifications())
                .language(entity.getLanguage())
                .timeFormat(entity.getTimeFormat())
                .timezone(entity.getTimezone())
                .build();

    }

    public Optional<User> findUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(ObjectId id) {
        userRepository.deleteById(id);
    }
    @Transactional
    public ApiResponse<?> deleteUserByUsername(String username) {
        try {
            User user = findByUserName(username);

            if (user.getJournalEntities() != null && !user.getJournalEntities().isEmpty()) {
                deleteListOfJournal(user.getJournalEntities());
            }

            userOtpRepository.deleteByUserName(username);
            userRepository.deleteByUserName(username);

            log.info("User '{}' deleted successfully", username);
            Map<String, String> res = new HashMap<>();
            res.put("userID", username);
            res.put("message", "Account deletion successful.");

            return ApiResponse.success(res, Constants.USER_ACCOUNT_DELETED_SUCCESSFULLY_MSG, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while deleting user '{}': {}", username, e.getMessage(), e);
            return ApiResponse.error(ErrorCode.EXCEPTION_WHILE_DELETING_USER_ACCOUNT, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public  void deleteListOfJournal(List<JournalEntries> journalEntriesList){
        journalEntityRepository.deleteAll(journalEntriesList);
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

    @Transactional
    public Boolean addAdmin(User user) {
        try {
            user.setRoles(Arrays.asList("USER", "ADMIN"));
            saveNewUser(user);
            log.info("User {} has been given created and have a ADMIN access");
            return true;
        }catch (Exception e){
            log.error("An error occur while adding a user : {}", e.getMessage());
        }
        return false;
    }

    public User findUserByEmail(String email) {
        User user=userRepository.findByUserName(email);
        if(user!=null){
            return user;
        }
        return null;
    }

    @Transactional
    public void sendForgetPasswordEmailOtp(User user) {
        Optional<UserOtp> optionalUserOtp = userOtpRepository.findByUserName(user.getUserName());
        if(!optionalUserOtp.isPresent()){
            throw new RuntimeException("User not found");
        }
        UserOtp userOtp = optionalUserOtp.get();
        userOtp.setOtp(generateOtp());
        userOtp.setOtpCreatedDateTime(LocalDateTime.now());
        userOtpRepository.save(userOtp);

        String subject = "OTP to Reset Your Account Password";
        String body = util.getBodyForResetPasswordSendOtpMail(user.getFirstName(), user.getUserName(), userOtp.getOtp());
//        emailService.sendEmailWithVelinqLogo(user.getUserName(), subject, body);
        emailService.sendEmailWithEmbeddedLogo(user.getUserName(), subject, body);
        log.info("Sent password reset OTP email to {}", user.getUserName());
    }

    public static String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"; // Define the allowed character set
        Random random = new Random();
        int length = random.nextInt(9) + 8; // Generate a random length between 8 and 16

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    public UserDto getUserDetail(String username) {
        try {
            User user= userRepository.findByUserName(username);
            if (user == null) {
                log.warn("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            UserDto userDtoResponse=convertUserToUserDto(user);
            return userDtoResponse;
        }catch (Exception e){
            log.info("Some Error get during extracting user details:  {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> verifyUser(String userName, String otp) {

        log.info("Email verification initiated");
        if(!util.isValidEmail(userName)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(ErrorCode.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST));
        }

        Optional<UserOtp> optionalUserOtp=userOtpRepository.findByUserName(userName);
        if(optionalUserOtp.isPresent()){

            UserOtp savedUserOtp = optionalUserOtp.get();
            User savedUser=userRepository.findByUserName(userName);

            if(savedUser.isVerified()){
                log.info("User already verified");
                return ResponseEntity.ok(ApiResponse.success(Constants.USER_ALREADY_VERIFIED, HttpStatus.OK));
            }

            if (StringUtils.isBlank(otp)) {
                log.info("OTP can not be null or empty for user: {}", userName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error(ErrorCode.OTP_MISSING_FROM_LINK,HttpStatus.BAD_REQUEST));
            } else if (StringUtils.isBlank(savedUserOtp.getOtp())) {
                log.warn("OTP is missing in DB for user: {}", userName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error(ErrorCode.OTP_NOT_FOUND_IN_DB,HttpStatus.BAD_REQUEST));
            } else if (!otp.equals(savedUserOtp.getOtp())) {
                log.info("The link you provided is invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error(ErrorCode.OTP_INVALID,HttpStatus.BAD_REQUEST));
            } else if (isOtpExpired(savedUserOtp.getOtpCreatedDateTime())) {
                log.info("The verification link has expired");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error(ErrorCode.OTP_EXPIRED,HttpStatus.BAD_REQUEST));
            }


            User user=findUserByEmail(userName);
            user.setVerified(true);
            User userVerified=userRepository.save(user);

            savedUserOtp.setOtp("");
            UserOtp userOtpUpdated=userOtpRepository.save(savedUserOtp);
            return ResponseEntity.ok(
                    ApiResponse.success(Constants.USER_VERIFICATION_SUCCESSFUL, HttpStatus.OK));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(ErrorCode.OTP_EXPIRED,HttpStatus.NOT_FOUND));
    }

    private boolean isValidOtp(String email, String otp){
        Optional<UserOtp> optionalUserOtp=userOtpRepository.findByUserName(email);

        if(otp==null || otp.trim().isEmpty()){
            throw new BadOtpException(ErrorCode.OTP_MISSING);
        } else if(optionalUserOtp.isEmpty()) {
           return false;
        }

        UserOtp savedUserOtp = optionalUserOtp.get();
        String savedOtp = savedUserOtp.getOtp();
        if(savedOtp==null || savedOtp.trim().isEmpty()){
            throw new BadOtpException(ErrorCode.OTP_ALREADY_USED_ERROR);
        }
        return otp.equals(savedOtp);
    }

    private boolean isOtpExpired(LocalDateTime otpCreatedDateTime) {
        LocalDateTime validDateTimeUntil=otpCreatedDateTime.plusMinutes(otpExpirationTimeInMinute);
        return LocalDateTime.now().isAfter(validDateTimeUntil);
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> resetPassword(PasswordRestRequest passwordRestRequest) {
        User user=userRepository.findByUserName(passwordRestRequest.getUserName());

        if(user==null){
            log.info("User not found: {}", passwordRestRequest.getUserName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        } else if(!isValidOtp(passwordRestRequest.getUserName(), passwordRestRequest.getOtp())){
            log.info("Invalid OTP for user: {}", passwordRestRequest.getUserName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.INVALID_OTP_ERROR, HttpStatus.BAD_REQUEST));
        }

        Optional<UserOtp> optionalUserOtp=userOtpRepository.findByUserName(user.getUserName());
        if(!optionalUserOtp.isPresent()){
            log.warn("OTP not found in DB for user: {}", user.getUserName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.OTP_NOT_FOUND_IN_DB, HttpStatus.BAD_REQUEST));
        }

        UserOtp userOtp = optionalUserOtp.get();
        if(isOtpExpired(userOtp.getOtpCreatedDateTime())){
            log.info("OTP expired for user: {}", user.getUserName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ErrorCode.OTP_EXPIRED_ERROR, HttpStatus.BAD_REQUEST));
        }


        // otp validated, now update password
        try {
            user.setPassword(passwordEncoder.encode(passwordRestRequest.getUpdatedPassword()));
            user.setUserUpdatedDate(LocalDateTime.now());
            user.setVerified(true);
            User savedUser = userRepository.save(user);

            userOtp.setOtp("");
            userOtp.setOtpCreatedDateTime(LocalDateTime.now());
            UserOtp savedUserOtp = userOtpRepository.save(userOtp);
            log.info("Password reset successful for user: {}", user.getUserName());
        }catch (Exception e){
            log.error("Exception during password reset for user {}: {}", user.getUserName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.FAILED_TO_RESET_PASSWORD, HttpStatus.INTERNAL_SERVER_ERROR));

        }

        return ResponseEntity.ok(ApiResponse.success(Constants.PASSWORD_RESET_SUCCESSFUL, HttpStatus.OK));
    }

    public ResponseEntity<ApiResponse<?>> updateRoleOfUser(String userName, boolean adminAccess) {
        log.info("Received request to update role for user: {}, grantAdmin: {}", userName, adminAccess);

        User user = userRepository.findByUserName(userName);
        if(user==null){
            log.warn("User not found: {}", userName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(ErrorCode.USER_NOT_FOUND, null, HttpStatus.NOT_FOUND));
        }

        List<String> roleOfUser =Optional.ofNullable(user.getRoles()).orElse(new ArrayList<>());
        log.debug("Current roles for user {}: {}", userName, roleOfUser);

        if(adminAccess){
            if(util.isAdmin(userName)){
                log.info("User {} already has admin access", userName);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ApiResponse.error(ErrorCode.USER_ALREADY_HAS_ADMIN_ACCESS, HttpStatus.CONFLICT));
            }

            roleOfUser.add(RoleEnum.ADMIN.name());
            log.info("Granted admin access to user: {}", userName);
        }
        else{
            if(!util.isAdmin(userName)){
                log.info("User {} already has only user access", userName);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ApiResponse.error(ErrorCode.USER_ALREADY_HAS_USER_ACCESS, HttpStatus.CONFLICT));
            }
            roleOfUser.removeIf(role->role.toUpperCase().contains(RoleEnum.ADMIN.name()));
            if(roleOfUser.isEmpty()) roleOfUser.add(RoleEnum.USER.name());
            log.info("Revoked admin access from user: {}", userName);
        }


        try {
            user.setRoles(roleOfUser);
            User saveUser=userRepository.save(user);
            log.info("User roles updated successfully for user: {}", userName);
            return ResponseEntity.ok(ApiResponse.success(saveUser, Constants.USER_ROLE_SUCCESSFULLY_MSG, HttpStatus.OK));

        }catch (Exception e){
            log.error("Exception while updating roles for user {}: {}", userName, e.getMessage(), e);
            throw new InternalServerErrorException(ErrorCode.EXCEPTION_WHILE_UPDATING_USER_ROLE, e.getCause());
        }

    }


    public double getActiveUserCount(){
        return (double) userRepository.findAll().stream()
                .filter(user->user.isVerified()==true)
                .count();
    }

    public double getUserCountWithNonEmptyJournals(){
        return (double) userRepository.findAll().stream()
                .filter(user -> !user.getJournalEntities().isEmpty())
                .count();
    }

    public double getUserCountWithAdminAccess() {
        return (double) userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains("ADMIN"))
                .count();
    }

    public UserDto updateUserProfilePhoto(String username, MultipartFile file) {
        log.info("Updating user's profile photo for username: {}", username);

        try {
            User savedUserInfo = findByUserName(username);
            if(savedUserInfo==null){
                log.warn("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username "+ username);
            }

            // Delete existing profile photo if any
            String existingProfileImageId = savedUserInfo.getProfileImageUrl();
            if (existingProfileImageId != null && !existingProfileImageId.isEmpty()) {
                try {
                    attachmentService.deleteAttachment(existingProfileImageId);
                    log.info("Deleted old profile photo with id: {}", existingProfileImageId);
                } catch (Exception e) {
                    log.warn("Failed to delete existing profile photo with id: {}. Continuing with update.", existingProfileImageId, e);
                }
            }

            // Save new attachment
            Attachment attachment = attachmentService.saveAttachment(file);
            savedUserInfo.setProfileImageUrl(attachment.getId().toString());

            // Save updated user
            User updatedUser = userRepository.save(savedUserInfo);
            return convertUserToUserDto(updatedUser);

        } catch (UsernameNotFoundException ex) {
            throw ex; // Keep original exception intact
        } catch (Exception e) {
            log.error("Error while updating user info in database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user profile photo", e);
        }
    }
}
