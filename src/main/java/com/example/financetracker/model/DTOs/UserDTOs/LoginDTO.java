package com.example.financetracker.model.DTOs.UserDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LoginDTO {

    @NotNull(message = "Email cannot be null!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotNull(message = "Password cannot be null!")
    @NotBlank(message = "Password cannot be blank!")
    private String password;

}
