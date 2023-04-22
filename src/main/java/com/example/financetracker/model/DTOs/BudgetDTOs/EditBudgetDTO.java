package com.example.financetracker.model.DTOs.BudgetDTOs;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditBudgetDTO {

    @NotBlank(message = "Description can not be empty!")
    @Size(max = 45, message = "Name should be up to 45 characters!")
    private String description;

    @FutureOrPresent(message = "Start date should be in future or present!")
    private LocalDateTime startDate;

    @Future(message = "End date should be in future!")
    private LocalDateTime endDate;

    @DecimalMin(value = "0.01", message = "Balance should be greater than zero!")
    private BigDecimal balance;

    @NotNull(message = "You must choose owner!")
    private int ownerId;

    @NotNull(message = "You must to choose any currency!")
    private int currencyId;

    @NotNull(message = "You must to choose any category!")
    private int categoryId;
}
