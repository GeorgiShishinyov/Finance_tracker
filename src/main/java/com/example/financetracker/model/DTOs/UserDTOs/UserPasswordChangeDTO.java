package com.example.financetracker.model.DTOs.UserDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPasswordChangeDTO {

    @NotNull(message = "Password cannot be null!")
    @NotBlank(message = "Password cannot be blank!")
    private String password;

    @NotNull(message = "New password cannot be null!")
    @NotBlank(message = "New password cannot be blank!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Invalid new password!")
    private String newPassword;

    @NotNull(message = "Confirm password cannot be null!")
    @NotBlank(message = "Confirm password cannot be blank!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Invalid confirm password!")
    private String confirmPassword;
}
