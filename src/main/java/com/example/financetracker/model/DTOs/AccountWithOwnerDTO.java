package com.example.financetracker.model.DTOs;

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
