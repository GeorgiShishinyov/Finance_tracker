package com.example.financetracker.model.DTOs.UserDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEditDTO {

    @Size(max = 15, message = "First name cannot be longer than 15 characters")
    @NotBlank(message = "First name cannot be blank!")
    private String firstName;

    @Size(max = 15, message = "Last name cannot be longer than 15 characters")
    @NotBlank(message = "Last name cannot be blank!")
    private String lastName;

    @NotNull(message = "Date of birth cannot be null!")
    @Past(message = "Invalid date of birth!")
    private LocalDateTime dateOfBirth;

}
