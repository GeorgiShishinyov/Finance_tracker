package com.example.financetracker.model.DTOs.UserDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPasswordChangeDTO {

    private String password;
    private String newPassword;
    private String confirmPassword;
}
