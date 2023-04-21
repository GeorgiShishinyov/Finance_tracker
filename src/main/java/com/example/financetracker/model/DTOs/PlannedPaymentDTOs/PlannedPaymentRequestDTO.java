package com.example.financetracker.model.DTOs.PlannedPaymentDTOs;

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
public class PlannedPaymentRequestDTO {

    private int accountId;
    private String description;
    private LocalDateTime date;
    private BigDecimal amount;
    private int categoryId;
    private int frequencyId;

}
