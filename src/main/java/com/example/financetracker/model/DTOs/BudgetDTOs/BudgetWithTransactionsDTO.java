package com.example.financetracker.model.DTOs.BudgetDTOs;

import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetWithTransactionsDTO extends DeleteBudgetDTO {

    private List<TransactionDTO> transactions;
}
