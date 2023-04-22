package com.example.financetracker.model.DTOs.AccountDTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class EditAccountDTO{

    @NotBlank(message = "The name of an account can not be empty!")
    @Size(max = 45, message = "Name should be up to 45 characters!")
    private String name;

    @DecimalMin(value = "0.01", message = "Balance should be greater than zero!")
    private BigDecimal balance;

    @NotNull(message = "You must to choose any currency!")
    private int currencyId;
}
