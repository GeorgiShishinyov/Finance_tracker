package com.example.financetracker.model.DTOs.TransferDTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {

    private int accountSenderId;
    private int accountReceiverId;
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 50, message = "Description cannot be longer than 50 characters")
    private String description;
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount should be greater than 0")
    private BigDecimal amount;

}
