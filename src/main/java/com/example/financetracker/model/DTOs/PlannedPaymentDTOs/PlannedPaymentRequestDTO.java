package com.example.financetracker.model.DTOs.PlannedPaymentDTOs;

import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 50, message = "Description cannot be longer than 50 characters")
    private String description;
    @NotNull(message = "Date cannot be null!")
    @FutureOrPresent(message = "Invalid date!")
    private LocalDateTime date;
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount should be greater than 0")
    private BigDecimal amount;
    private int categoryId;
    private int frequencyId;

}
