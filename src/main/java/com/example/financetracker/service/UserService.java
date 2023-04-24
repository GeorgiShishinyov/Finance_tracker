package com.example.financetracker.service;

import com.example.financetracker.SessionCollector;
import com.example.financetracker.model.DTOs.UserDTOs.*;
import com.example.financetracker.model.entities.LoginLocation;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserService extends AbstractService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SessionCollector sessionCollector;

    public UserFullInfoDTO register(RegisterDTO dto) {
        checkMatchingPasswords(dto.getPassword(), dto.getConfirmPassword());
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("This email already exists.");
        }
        User user = mapper.map(dto, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setLastLogin(LocalDateTime.now());
        // Generate a unique code using UUID, set it to the user object,
        // and set its expiration date to 24 hours from now
        String uniqueCode = UUID.randomUUID().toString();
        user.setUniqueCode(uniqueCode);
        user.setExpirationDate(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        logger.info("Registered user: " + user.toString());
        new Thread(() -> {
            sendEmailValidation(user.getEmail(), uniqueCode);
        }).start();

        return mapper.map(user, UserFullInfoDTO.class);
    }

    @Transactional
    public UserFullInfoDTO login(LoginDTO dto, String ip) {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        User user = verifyUserExistence(optionalUser);
        checkCorrectCredentials(dto.getPassword(), user.getPassword());
        if (!user.isVerified()) {
            throw new UnauthorizedException("The user is not verified. Please check your email.");
        }
        // Check if a login location for the given IP and user ID already exists
        if (!loginLocationRepository.existsByIpAndUser_Id(ip, user.getId())) {
            LoginLocation loginLocation = new LoginLocation();
            loginLocation.setUser(user);
            loginLocation.setIp(ip);
            loginLocationRepository.save(loginLocation);
            //Send an email to the user with a unique code for invalidating the IP address in case it was compromised
            new Thread(() -> {
                String ipUniqueCode = UUID.randomUUID().toString();
                sendEmailIpInvalidation(user.getEmail(), user.getId(), ipUniqueCode);
            }).start();
        }
        String sms2FACode = generateCode();
        user.setSms2FACode(sms2FACode);
        user.setSmsExpirationDate(LocalDateTime.now().plusMinutes(5));
        new Thread(() -> {
            // Send SMS for 2FA authentication
            send2FASms(user.getPhoneNumber(), sms2FACode);
        }).start();

        return mapper.map(user, UserFullInfoDTO.class);
    }

    public UserFullInfoDTO updateUserById(int id, UserEditDTO editDto, int loggedUserId) {
        Optional<User> optionalUser = userRepository.findById(id);
        checkAuthorization(id, loggedUserId);
        User user = verifyUserExistence(optionalUser);
        user.setFirstName(editDto.getFirstName());
        user.setLastName(editDto.getLastName());
        user.setDateOfBirth(editDto.getDateOfBirth());
        userRepository.save(user);
        logger.info("Updated user: " + user.getId() + "\n" + user.toString());

        return mapper.map(user, UserFullInfoDTO.class);
    }

    public UserFullInfoDTO deleteUserById(int id, int loggedUserId) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = verifyUserExistence(optionalUser);
        checkAuthorization(id, loggedUserId);
        userRepository.deleteById(id);
        logger.info("Deleted user: " + user.getId() + "\n" + user.toString());

        return mapper.map(user, UserFullInfoDTO.class);
    }

    public UserFullInfoDTO validateCode(String code) {
        User user = userRepository.findByUniqueCodeAndExpirationDateBefore(code, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Validation code not found or has expired."));

        user.setVerified(true);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return mapper.map(user, UserFullInfoDTO.class);
    }

    public UserFullInfoDTO changePassword(int id, UserPasswordChangeDTO passwordChangeDTO, int loggedUserId) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = verifyUserExistence(optionalUser);
        checkAuthorization(id, loggedUserId);
        checkCorrectCredentials(passwordChangeDTO.getPassword(), user.getPassword());
        checkMatchingPasswords(passwordChangeDTO.getNewPassword(), passwordChangeDTO.getConfirmPassword());
        user.setPassword(encoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);
        logger.info("Updated user's password: " + user.getId() + "\n" + user.toString());

        return mapper.map(user, UserFullInfoDTO.class);
    }

    @Transactional
    public ResponseEntity<String> invalidateSessions(Integer id) {
        for (HttpSession s : sessionCollector.getAllSessions()) {
            if (s.getAttribute("LOGGED_ID") != null && s.getAttribute("LOGGED_ID").equals(id)) {
                s.invalidate();
            }
        }
        loginLocationRepository.deleteAllByUserId(id);

        return ResponseEntity.ok("Sessions invalidated.");
    }

    @SneakyThrows
    private void sendEmailValidation(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("finance.tracker.app2023@gmail.com");
        message.setTo(email);
        message.setSubject("Email Validation");
        message.setText("""
                Hi,
                                
                Please click the following link to validate your email: http://localhost:7777/email-validation?code=""" + code + """
                                
                Best regards,
                The Finance tracker team""");
        javaMailSender.send(message);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredCodes() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<User> expiredUsers = userRepository.findByIsVerifiedAndExpirationDateBefore(false, currentTime);
        userRepository.deleteAll(expiredUsers);
    }

    private void sendEmailIpInvalidation(String email, int id, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("finance.tracker.app2023@gmail.com");
        message.setTo(email);
        message.setSubject("Security alert for you linked Finance tracker account");
        message.setText("""
                Hi,
                                
                Your Finance tracker account was just signed in to from a new device. You're getting this email to make sure it was you.
                        
                If you did not perform this action, please click the following link to sign out of all devices:
                http://localhost:7777/users/""" + id + """
                /invalidate?code=""" + code + """
                                        
                We recommend that you change your password.
                                
                Best regards,
                The Finance tracker team""");

        javaMailSender.send(message);
    }

    private void checkMatchingPasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("The passwords do not match!");
        }
    }

    private User verifyUserExistence(Optional<User> optionalUser) {
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User not found.");
        }

        return optionalUser.get();
    }

    private void checkCorrectCredentials(String password, String password1) {
        if (!encoder.matches(password, password1)) {
            throw new UnauthorizedException("Incorrect credentials.");
        }
    }

    private void checkAuthorization(int id, int loggedUserId) {
        if (id != loggedUserId) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
    }

    @Value("${sms.api.key}")
    private String apiKey;
    @Value("${sms.api.secret}")
    private String apiSecret;

    private void send2FASms(String to, String sms2FACode) {
        VonageClient client = VonageClient.builder().apiKey(apiKey).apiSecret(apiSecret).build();
        TextMessage message = new TextMessage("Vonage APIs",
                to,
                sms2FACode
        );
        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }

    public UserFullInfoDTO confirmSmsCode(int userId, String code) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = verifyUserExistence(optionalUser);

        if (!user.getSms2FACode().equals(code)) {
            throw new UnauthorizedException("Confirmation code is invalid.");
        }
        if (LocalDateTime.now().isAfter(user.getSmsExpirationDate())) {
            throw new UnauthorizedException("Confirmation code is expired");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        logger.info("Logged-in user: " + user.getId() + "\n" + user.toString());
        return mapper.map(user, UserFullInfoDTO.class);
    }

    private static String generateCode() {
        Random rand = new Random();
        int numDigits = 6;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < numDigits; i++) {
            int digit = rand.nextInt(10);
            sb.append(digit);
        }

        String code = sb.toString();
        return code;
    }
}
