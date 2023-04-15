package com.example.financetracker.model.DTOs;

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
public class TransactionRequestDTO {

    private String description;
    private LocalDateTime date;
    private int accountId;
    private BigDecimal amount;
    private int categoryId;

}
