package com.example.financetracker.model.DTOs.АccountDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {

    private String name;
    private BigDecimal balance;
    private int currencyId;

}
