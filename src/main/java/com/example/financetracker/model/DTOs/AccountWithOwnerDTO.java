package com.example.financetracker.model.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountWithOwnerDTO extends AccountWithoutOwnerDTO{

    private UserWithoutPassDTO owner;
}
