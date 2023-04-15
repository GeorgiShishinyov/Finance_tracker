package com.example.financetracker.model.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class CreateAccountDTO {

    private String name;
    private BigDecimal balance;
    private int currency;

}
