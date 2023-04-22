package com.example.financetracker.model.DTOs.TransactionDTOs;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
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
public class TransactionRequestDTO {

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 50, message = "Description cannot be longer than 50 characters")
    private String description;
    @NotNull(message = "Date cannot be null!")
    @PastOrPresent(message = "Invalid date!")
    private LocalDateTime date;
    private int accountId;
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount should be greater than 0")
    private BigDecimal amount;
    private int currencyId;
    private int categoryId;
    private Integer plannedPaymentId;

}
