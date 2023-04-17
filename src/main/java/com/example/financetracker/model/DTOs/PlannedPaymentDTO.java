package com.example.financetracker.model.DTOs;

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
public class PlannedPaymentDTO {

    private int id;
    private String description;
    private LocalDateTime date;
    private BigDecimal amount;
    private AccountWithoutOwnerDTO account;
    private CategoryDTO category;
    private Frequency frequency;

}