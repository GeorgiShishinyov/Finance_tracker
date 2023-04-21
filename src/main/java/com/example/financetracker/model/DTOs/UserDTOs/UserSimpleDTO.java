package com.example.financetracker.model.DTOs.UserDTOs;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserSimpleDTO {

    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfBirth;
    private LocalDateTime lastLogin;
}
