package com.example.financetracker.model.DTOs.UserDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfBirth;

}
