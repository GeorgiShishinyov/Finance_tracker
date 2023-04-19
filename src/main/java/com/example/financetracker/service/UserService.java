package com.example.financetracker.service;

import com.example.financetracker.SessionCollector;
import com.example.financetracker.model.DTOs.*;
import com.example.financetracker.model.entities.LoginLocation;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
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

    private static final Logger logger = LogManager.getLogger(UserService.class);


    public UserFullInfoDTO register(RegisterDTO dto) {
        logger.info("1. Starting the registration process for email: {}", dto.getEmail());

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            logger.error("2. Passwords do not match.");
            throw new BadRequestException("The passwords do not match!");
        }
        if (!isStrongPassword(dto.getPassword())) {
            logger.error("3. Weak password.");
            throw new BadRequestException("Weak password. The password must be " +
                    "at least 8 characters long and contain an uppercase letter, " +
                    "a lowercase letter, a number, and a special character.");
        }
        if (!isValidEmail(dto.getEmail())) {
            logger.error("4. Invalid email.");
            throw new BadRequestException("Invalid email!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            logger.error("5. Email already exists.");
            throw new BadRequestException("This email already exists.");
        }
        logger.info("6. The email is valid.");

        User u = mapper.map(dto, User.class);
        u.setPassword(encoder.encode(u.getPassword()));
        u.setLastLogin(LocalDateTime.now());
        String uniqueCode = UUID.randomUUID().toString();
        u.setUniqueCode(uniqueCode);
        u.setExpirationDate(LocalDateTime.now().plusHours(24));
        userRepository.save(u);
        logger.info("7. User saved to the repository.");

        new Thread(() -> {
            logger.info("8. Sending email validation for email: {}", u.getEmail());
            sendEmailValidation(u.getEmail(), uniqueCode);
        }).start();

        logger.info("9. Registration process completed for email: {}", dto.getEmail());
        return mapper.map(u, UserFullInfoDTO.class);
    }

    @Transactional
    public UserFullInfoDTO login(LoginDTO dto, String ip) {
        Optional<User> u = userRepository.findByEmail(dto.getEmail());
        if (!u.isPresent()) {
            throw new UnauthorizedException("Incorrect credentials.");
        }
        if (!encoder.matches(dto.getPassword(), u.get().getPassword())) {
            throw new UnauthorizedException("Incorrect credentials.");
        }
        if (!loginLocationRepository.existsByIpAndUser_Id(ip, u.get().getId())) {
            LoginLocation loginLocation = new LoginLocation();
            loginLocation.setUser(u.get());
            loginLocation.setIp(ip);
            loginLocationRepository.save(loginLocation);
            new Thread(() -> {
                String ipUniqueCode = UUID.randomUUID().toString();
                sendEmailIpInvalidation(u.get().getEmail(), u.get().getId(), ipUniqueCode);
            }).start();
        }
        u.get().setLastLogin(LocalDateTime.now());
        userRepository.save(u.get());
        return mapper.map(u.get(), UserFullInfoDTO.class);
    }

    @Transactional
    public UserFullInfoDTO updateUserById(Integer id, UserEditDTO editDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User not found.");
        }
        User user = optionalUser.get();
        user.setFirstName(editDto.getFirstName());
        user.setLastName(editDto.getLastName());
        user.setDateOfBirth(editDto.getDateOfBirth());
        userRepository.save(user);
        return mapper.map(user, UserFullInfoDTO.class);
    }

    public UserFullInfoDTO changePassword(Integer id, UserPasswordChangeDTO passwordChangeDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User not found.");
        }
        User user = optionalUser.get();
        if (!encoder.matches(passwordChangeDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect password.");
        }
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new BadRequestException("The passwords do not match!");
        }
        if (!isStrongPassword(passwordChangeDTO.getNewPassword())) {
            throw new BadRequestException("Weak password. The password must be " +
                    "at least 8 characters long and contain an uppercase letter, " +
                    "a lowercase letter, a number, and a special character.");

        }
        user.setPassword(encoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);
        return mapper.map(user, UserFullInfoDTO.class);
    }

    public UserFullInfoDTO deleteUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User not found.");
        }
        User user = optionalUser.get();
        userRepository.deleteById(id);
        return mapper.map(user, UserFullInfoDTO.class);
    }

    private boolean isStrongPassword(String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }

    private boolean isValidEmail(String email) {
        String pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(pattern);
    }

    public UserFullInfoDTO validateCode(String code) {
        User user = userRepository.findByUniqueCodeAndExpirationDateBefore(code, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Validation code not found or has expired."));

        user.setVerified(true);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return mapper.map(user, UserFullInfoDTO.class);
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

    @Transactional
    public ResponseEntity<String> invalidateSessions(Integer userId) {
        for (HttpSession s : sessionCollector.getAllSessions()) {
            if (s.getAttribute("LOGGED_ID") != null && s.getAttribute("LOGGED_ID") == userId) {
                s.invalidate();
            }
        }
        loginLocationRepository.deleteAllByUserId(userId);
        return ResponseEntity.ok("Sessions invalidated.");
    }
}
