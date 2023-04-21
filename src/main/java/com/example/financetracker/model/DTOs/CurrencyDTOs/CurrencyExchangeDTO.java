package com.example.financetracker.model.DTOs.CurrencyDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyExchangeDTO {

        private String success;
        private BigDecimal result;
}
