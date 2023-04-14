package com.example.financetracker.model.DTOs;

import com.example.financetracker.model.entities.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithoutOwnerDTO {

    private int id;
    private String name;
    private BigDecimal balance;
    private Currency currency;
}
