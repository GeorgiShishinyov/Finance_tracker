package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.DTOs.TransactionRequestDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService extends AbstractService{

    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    public TransactionDTO createTransaction(TransactionRequestDTO transactionRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Account account = getAccountById(transactionRequestDTO.getAccountId());
        if (!account.getOwner().equals(user)){
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
        Category category = getCategoryById(transactionRequestDTO.getCategoryId());
        if (account.getBalance().compareTo(transactionRequestDTO.getAmount()) <= 0) {
            throw new UnauthorizedException("Insufficient funds in sender account.");
        }
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setDescription(transactionRequestDTO.getDescription());
        transaction.setAccount(account);
        transaction.setCategory(category);

        account = adjustAccountBalanceOnCreate(account, transaction);
        accountRepository.save(account);
        transactionRepository.save(transaction);
        return mapper.map(transaction,TransactionDTO.class);
    }
    @Transactional
    public TransactionDTO deleteTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkAccessRights(transaction, user);
        Account account = transaction.getAccount();
        account = adjustAccountBalanceOnDelete(account, transaction);
        accountRepository.save(account);
        transactionRepository.delete(transaction);
        return mapper.map(transaction,TransactionDTO.class);
    }

    @Transactional
    public TransactionDTO editTransactionById(int transactionId, TransactionEditRequestDTO transactionEditRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkAccessRights(transaction, user);
        Account account = transaction.getAccount();
        Category category = getCategoryById(transactionEditRequestDTO.getCategoryId());
        account = adjustAccountBalanceOnDelete(account, transaction);
        accountRepository.save(account);
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(transactionEditRequestDTO.getAmount());
        transaction.setDescription(transactionEditRequestDTO.getDescription());
        transaction.setCategory(category);
        account = adjustAccountBalanceOnCreate(account, transaction);
        accountRepository.save(account);
        transactionRepository.save(transaction);
        return mapper.map(transaction,TransactionDTO.class);
    }

    public TransactionDTO findTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkAccessRights(transaction, user);
        return mapper.map(transaction, TransactionDTO.class);
    }

    @Transactional
    public List<TransactionDTO> getAllTransactionsForUser(int userId, int loggedUserId) {
        if (userId != loggedUserId) {
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
        User user = getUserById(userId);
        List<Transaction> transactions = transactionRepository.findAllByAccount_Owner(user);
        if (transactions.isEmpty()) {
            throw new NotFoundException("Transactions not found");
        }
        return transactions.stream()
                .map(transaction -> mapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TransactionDTO> getAllTransactionsForAccount(int accountId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Account account = getAccountById(accountId);
        if (!account.getOwner().equals(user)) {
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
        List<Transaction> transactions = transactionRepository.findAllByAccount(account);
        if (transactions.isEmpty()) {
            throw new NotFoundException("Transactions not found");
        }
        return transactions.stream()
                .map(transaction -> mapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    private Account adjustAccountBalanceOnDelete(Account account, Transaction transaction){
        BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal newBalance = account.getBalance();
        if (transaction.getCategory().getType() == Category.CategoryType.INCOME) {
            newBalance = newBalance.subtract(transactionAmount);
        } else {
            newBalance = newBalance.add(transactionAmount);
        }
        account.setBalance(newBalance);
        return account;
    }

    private Account adjustAccountBalanceOnCreate(Account account, Transaction transaction){
        BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal newBalance = account.getBalance();
        if (transaction.getCategory().getType() == Category.CategoryType.INCOME) {
            newBalance = newBalance.add(transactionAmount);
        } else {
            newBalance = newBalance.subtract(transactionAmount);
        }
        account.setBalance(newBalance);
        return account;
    }

    private void checkAccessRights(Transaction transaction, User user){
        if (!transaction.getAccount().getOwner().equals(user)){
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
    }

}
