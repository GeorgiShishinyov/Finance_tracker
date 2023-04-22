package com.example.financetracker.model.DTOs.AccountDTOs;

import com.example.financetracker.model.DTOs.UserDTOs.UserFullInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithOwnerDTO extends AccountWithoutOwnerDTO{

    private UserFullInfoDTO owner;
}
