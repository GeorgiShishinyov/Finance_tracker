package com.example.financetracker.model.DTOs.TransactionDTOs;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.model.DTOs.PlannedPaymentDTOs.PlannedPaymentSimpleDTO;
import com.example.financetracker.model.DTOs.AccountDTOs.AccountWithoutOwnerDTO;
import com.example.financetracker.model.entities.Category;
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
public class TransactionDTO {

    private int id;
    private String description;
    private LocalDateTime date;
    private BigDecimal amount;
    private CurrencyDTO currencyDTO;
    private AccountWithoutOwnerDTO account;
    private Category category;
    private PlannedPaymentSimpleDTO plannedPayment;

}
