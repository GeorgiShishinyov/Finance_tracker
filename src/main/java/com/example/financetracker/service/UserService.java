package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.*;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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

    public UserFullInfoDTO register(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("The passwords do not match!");
        }
        if (!isStrongPassword(dto.getPassword())) {
            throw new BadRequestException("Weak password. The password must be " +
                    "at least 8 characters long and contain an uppercase letter, " +
                    "a lowercase letter, a number, and a special character.");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new BadRequestException("Invalid email!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("This email already exists.");
        }
        User u = mapper.map(dto, User.class);
        u.setPassword(encoder.encode(u.getPassword()));
        u.setLastLogin(LocalDateTime.now());
        String uniqueCode = UUID.randomUUID().toString();
        u.setUniqueCode(uniqueCode);
        u.setExpirationDate(LocalDateTime.now().plusHours(24));
        userRepository.save(u);
        new Thread(() -> {
            sendEmailValidation(u.getEmail(), uniqueCode);
        }).start();
        return mapper.map(u, UserFullInfoDTO.class);
    }

    public UserFullInfoDTO login(LoginDTO dto) {
        Optional<User> u = userRepository.findByEmail(dto.getEmail());
        if (!u.isPresent()) {
            throw new UnauthorizedException("Incorrect credentials.");
        }
        if (!encoder.matches(dto.getPassword(), u.get().getPassword())) {
            throw new UnauthorizedException("Incorrect credentials.");
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
        message.setText("Please click the following link to validate your email: http://localhost:7777/email-validation?code=" + code);
        javaMailSender.send(message);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredCodes() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<User> expiredUsers = userRepository.findByIsVerifiedAndExpirationDateBefore(false, currentTime);
        userRepository.deleteAll(expiredUsers);
    }

}
