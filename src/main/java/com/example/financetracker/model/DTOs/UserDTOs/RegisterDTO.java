package com.example.financetracker.model.DTOs.UserDTOs;

import jakarta.validation.constraints.*;
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

    @NotNull(message = "Email cannot be null!")
    @Email(message = "Invalid email!", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;

    @NotNull(message = "Password cannot be null!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Invalid password!")
    private String password;

    @NotNull(message = "Confirm password cannot be null!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Invalid confirm password!")
    private String confirmPassword;


    @NotBlank(message = "First name cannot be blank!")
    @Size(max = 15, message = "First name cannot be longer than 15 characters")
    private String firstName;


    @NotBlank(message = "Last name cannot be blank!")
    @Size(max = 15, message = "Last name cannot be longer than 15 characters")
    private String lastName;

    @NotNull(message = "Date of birth cannot be null!")
    @Past(message = "Invalid date of birth!")
    private LocalDateTime dateOfBirth;

}
