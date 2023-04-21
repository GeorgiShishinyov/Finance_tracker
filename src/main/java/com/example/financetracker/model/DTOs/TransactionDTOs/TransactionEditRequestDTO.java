package com.example.financetracker.model.DTOs.TransactionDTOs;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
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
public class TransactionEditRequestDTO {

    private String description;
    private LocalDateTime date;
    private BigDecimal amount;
    private int currencyId;
    private int categoryId;
}
