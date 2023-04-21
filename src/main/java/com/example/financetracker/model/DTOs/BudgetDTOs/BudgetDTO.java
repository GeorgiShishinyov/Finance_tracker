package com.example.financetracker.model.DTOs.BudgetDTOs;

import com.example.financetracker.model.DTOs.CategoryDTOs.CategoryDTO;
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
public class BudgetDTO {

    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal balance;
    private CurrencyDTO currency;
    private CategoryDTO category;
}
