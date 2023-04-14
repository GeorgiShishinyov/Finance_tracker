package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.RegisterDTO;
import com.example.financetracker.model.DTOs.UserFullInfoDTO;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService extends AbstractService{

   @Autowired
   private BCryptPasswordEncoder encoder;


   public UserFullInfoDTO register(RegisterDTO dto) {
      if(!dto.getPassword().equals(dto.getConfirmPassword())){
         throw new BadRequestException("The passwords do not match!");
      }
      if(!isStrongPassword(dto.getPassword())){
         throw new BadRequestException("Weak password. The password must be " +
                 "at least 8 characters long and contain an uppercase letter, " +
                 "a lowercase letter, a number, and a special character.");
      }
      if(!isValidEmail(dto.getEmail())){
         throw new BadRequestException("Invalid email!");
      }
      if(userRepository.existsByEmail(dto.getEmail())){
         throw new BadRequestException("This email already exists.");
      }
      User u = mapper.map(dto, User.class);
      u.setPassword(encoder.encode(u.getPassword()));
      u.setLastLogin(LocalDateTime.now()); //TODO
      userRepository.save(u);
      new Thread(() -> {
         //TODO MailSender.sendEmail(u.getEmail(), "Registration successful");
      }).start();
      return mapper.map(u, UserFullInfoDTO.class);
   }

   private boolean isStrongPassword(String password) {
      String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
      return password.matches(pattern);
   }

   private boolean isValidEmail(String email) {
      String pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
      return email.matches(pattern);
   }
}
