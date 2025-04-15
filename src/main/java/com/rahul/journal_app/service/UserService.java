package com.rahul.journal_app.service;

import com.rahul.journal_app.constants.Constants;
import com.rahul.journal_app.constants.ErrorCode;
import com.rahul.journal_app.entity.Attachment;
import com.rahul.journal_app.entity.JournalEntries;
import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.entity.UserOtp;
import com.rahul.journal_app.enums.Gender;
import com.rahul.journal_app.exception.BadRequestException;
import com.rahul.journal_app.model.UserDto;
import com.rahul.journal_app.repository.JournalEntityRepository;
import com.rahul.journal_app.repository.UserOtpRepository;
import com.rahul.journal_app.repository.UserRepository;
import com.rahul.journal_app.request.PasswordRestRequest;
import com.rahul.journal_app.utils.Util;
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
        if(user.getEmail()==null || user.getEmail().isEmpty()){
            user.setEmail(user.getUserName());
        }
        user.setUserUpdatedDate(LocalDateTime.now());
        User savedUser=userRepository.save(user);

        UserOtp userOtp = new UserOtp();
        userOtp.setUserName(user.getUserName());
        //userOtp.setEmail(user.getUserName());
        userOtp.setOtp(generateOtp());
        userOtp.setOtpCreatedDateTime(LocalDateTime.now());

        UserOtp userOtpSaved = userOtpRepository.save(userOtp);
        sendOtpVerificationEmail(savedUser.getFirstName(), userOtpSaved.getUserName(), userOtpSaved.getOtp());
        log.info("User Successfully Registered: {}", user.getUserName());
    }

    private void sendOtpVerificationEmail(String firstName, String userName, String otp) {

        String subject = "Verify Your Email Address for JournalApp Registration";
        String body = util.getBodyForOtpVerificationMail(firstName, userName, otp);
        emailService.sendMail(userName, subject, body);
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

    public User saveUserEntry(User user){
        User savedUser = userRepository.save(user);
        return savedUser;
    }

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
        log.info("hi");



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
    public String deleteUserByUsername(String username){
        try {
            User user = findByUserName(username);
            if (!user.getJournalEntities().isEmpty()) {
                deleteListOfJournal(user.getJournalEntities());
            }
            userOtpRepository.deleteByUserName(username);
            userRepository.deleteByUserName(username);
            return "user " + username + " deleted";
        }catch (Exception e){
            log.error("Error: {}", e.getMessage());
            throw new RuntimeException(e);
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
        emailService.sendMail(user.getUserName(), subject, body);
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
    public ResponseEntity<?> verifyUser(String userName, String otp) {

        log.info("user verification ");
        if(!util.isValidEmail(userName)){
            return new ResponseEntity<>(Constants.INVALID_EMAIL_FORMAT, HttpStatus.BAD_REQUEST);
        }
        Optional<UserOtp> optionalUserOtp=userOtpRepository.findByUserName(userName);
        if(optionalUserOtp.isPresent()){

            UserOtp savedUserOtp = optionalUserOtp.get();
            User savedUser=userRepository.findByUserName(userName);
            if(savedUser.isVerified()){
                log.info("User already verified");
                return new ResponseEntity<>(Constants.USER_ALREADY_VERIFIED, HttpStatus.OK);
            }
            if (otp == null || otp.equals("")) {
                log.info("OTP can not be null or empty");
                return new ResponseEntity<>(Constants.OTP_NULL_OR_EMPTY_EXCEPTION, HttpStatus.BAD_REQUEST);
            } else if (savedUserOtp.getOtp() == null || savedUserOtp.getOtp().equals("")) {
                log.info("Invalid OTP");
                return new ResponseEntity<>(Constants.INVALID_LINK, HttpStatus.BAD_REQUEST);
            } else if (!otp.equals(savedUserOtp.getOtp())) {
                log.info("The link you provided is invalid");
                return new ResponseEntity<>(Constants.INVALID_LINK, HttpStatus.BAD_REQUEST);
            } else if (isOtpExpired(savedUserOtp.getOtpCreatedDateTime())) {
                log.info("The verification link has expired");
                return new ResponseEntity<>(Constants.LINK_EXPIRED, HttpStatus.BAD_REQUEST);
            }

            User user=findUserByEmail(userName);
            user.setVerified(true);
            User userVerified=userRepository.save(user);

            savedUserOtp.setOtp("");
            UserOtp userOtpUpdated=userOtpRepository.save(savedUserOtp);
            return new ResponseEntity<>(Constants.USER_VERIFICATION_SUCCESSFUL, HttpStatus.OK);
        }
        return new ResponseEntity<>(Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }

    private boolean isValidOtp(String email, String otp){
        Optional<UserOtp> optionalUserOtp=userOtpRepository.findByUserName(email);
        if(optionalUserOtp.isPresent()) {
            UserOtp savedUserOtp = optionalUserOtp.get();
            if (otp == null || otp.equals("")) {
                throw new RuntimeException(Constants.OTP_NULL_OR_EMPTY_EXCEPTION);
            }else if (savedUserOtp.getOtp() == null || savedUserOtp.getOtp().equals("")) {
                throw new RuntimeException("Invalid OTP");
            }
            if(otp.equals(savedUserOtp.getOtp())) return true;
            else return false;
        }
        return false;
    }

    private boolean isOtpExpired(LocalDateTime otpCreatedDateTime) {
        LocalDateTime validDateTimeUntil=otpCreatedDateTime.plusMinutes(otpExpirationTimeInMinute);
        return LocalDateTime.now().isAfter(validDateTimeUntil);
    }

    @Transactional
    public ResponseEntity<?> resetPassword(PasswordRestRequest passwordRestRequest) {
        User user=userRepository.findByUserName(passwordRestRequest.getUserName());
        if(user==null){
            log.info("User not found");
            return new ResponseEntity<>(Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        } else if(!isValidOtp(passwordRestRequest.getUserName(), passwordRestRequest.getOtp())){
            log.info("Invalid OTP");
            return new ResponseEntity<>(Constants.INVALID_OTP_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        Optional<UserOtp> optionalUserOtp=userOtpRepository.findByUserName(user.getUserName());
        if(!optionalUserOtp.isPresent()){
            return new ResponseEntity<>(Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        UserOtp userOtp = optionalUserOtp.get();
        if(isOtpExpired(userOtp.getOtpCreatedDateTime())){
            return new ResponseEntity<>(Constants.OTP_EXPIRED, HttpStatus.BAD_REQUEST);
        }
        // otp validated
        try {
            user.setPassword(passwordEncoder.encode(passwordRestRequest.getUpdatedPassword()));
            user.setUserUpdatedDate(LocalDateTime.now());
            user.setVerified(true);
            User savedUser = userRepository.save(user);

            userOtp.setOtp("");
            userOtp.setOtpCreatedDateTime(LocalDateTime.now());
            UserOtp savedUserOtp = userOtpRepository.save(userOtp);
        }catch (Exception e){
            log.error("Error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(Constants.PASSWORD_RESET_SUCCESSFUL, HttpStatus.OK);
    }

    public ResponseEntity<?> updateRoleOfUser(User user, boolean adminAccess) {
        try {
            List<String> roleOfUser = user.getRoles();
            log.info("We are providing admin access to the user: {}", adminAccess);
            if(adminAccess) { // grant admin access
                roleOfUser.add("ADMIN");
            }else{ // remove admin access
                List<String> roleWithoutADMIN=roleOfUser.stream()
                        .filter(r->!r.toUpperCase().contains("ADMIN"))
                        .collect(Collectors.toList());
                roleOfUser=roleWithoutADMIN;
            }
            user.setRoles(roleOfUser);
            User saveUser=userRepository.save(user);
            return new ResponseEntity<>(saveUser, HttpStatus.OK);
        }catch (Exception e){
            log.info("Error while saving in db {}",e.getMessage());
            throw new RuntimeException(e);
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
