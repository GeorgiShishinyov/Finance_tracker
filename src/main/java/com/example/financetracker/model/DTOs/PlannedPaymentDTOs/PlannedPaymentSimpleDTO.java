package com.example.financetracker.model.DTOs.PlannedPaymentDTOs;

import com.example.financetracker.model.DTOs.AccountDTOs.AccountWithoutOwnerDTO;
import com.example.financetracker.model.entities.Frequency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlannedPaymentSimpleDTO {

    private int id;
    private String description;
    private LocalDateTime date;
    private BigDecimal amount;
    private AccountWithoutOwnerDTO account;
    private Frequency frequency;

}
