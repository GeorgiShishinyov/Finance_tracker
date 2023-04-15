package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.TransactionDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService extends AbstractService{

    @Autowired
    private TransactionRepository transactionRepository;
    @Transactional
    public TransactionDTO deleteTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        if (!transaction.getAccount().getOwner().equals(user)){
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
        BigDecimal transactionAmount = transaction.getAmount();
        Account account = transaction.getAccount();
        BigDecimal newBalance = account.getBalance().add(transactionAmount);
        if (transaction.getCategory().getType() == Category.CategoryType.INCOME) {
            newBalance = newBalance.subtract(transactionAmount);
        }
        account.setBalance(newBalance);
        accountRepository.save(account);
        transactionRepository.delete(transaction);
        return mapper.map(transaction,TransactionDTO.class);
    }
}
