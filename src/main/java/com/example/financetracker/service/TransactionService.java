package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.DTOs.TransactionRequestDTO;
import com.example.financetracker.model.entities.*;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService extends AbstractService {

    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    public TransactionDTO createTransaction(TransactionRequestDTO transactionRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Account account = getAccountById(transactionRequestDTO.getAccountId());
        if (!account.getOwner().equals(user)) {
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
        Integer plannedPaymentId = transactionRequestDTO.getPlannedPaymentId();
        //TODO for refactoring
        if(plannedPaymentId != null){
            PlannedPayment plannedPayment = getPlannedPaymentById(plannedPaymentId);
            transaction.setPlannedPayment(plannedPayment);
        }
        account = adjustAccountBalanceOnCreate(account, transaction);
        accountRepository.save(account);
        transactionRepository.save(transaction);
        return mapper.map(transaction, TransactionDTO.class);
    }

    @Transactional
    public TransactionDTO deleteTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkTransactionAccessRights(transaction, user);
        Account account = transaction.getAccount();
        account = adjustAccountBalanceOnDelete(account, transaction);
        accountRepository.save(account);
        transactionRepository.delete(transaction);
        return mapper.map(transaction, TransactionDTO.class);
    }

    @Transactional
    public TransactionDTO editTransactionById(int transactionId, TransactionEditRequestDTO transactionEditRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkTransactionAccessRights(transaction, user);
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
        return mapper.map(transaction, TransactionDTO.class);
    }

    public TransactionDTO findTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkTransactionAccessRights(transaction, user);
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

    public List<TransactionDTO> getFilteredTransactions(LocalDateTime startDate, LocalDateTime endDate, Integer categoryId, Integer accountId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Account account = getAccountById(accountId);
        if (!account.getOwner().equals(user)) {
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
        Category category = getCategoryById(categoryId);
        dateValidation(startDate, endDate);
        List<Transaction> transactions = transactionRepository.findByDateBetweenAndCategoryAndAccount(startDate, endDate, category, account);
        if (transactions.isEmpty()) {
            throw new NotFoundException("Transactions not found");
        }
        return transactions.stream()
                .map(transaction -> mapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    private Account adjustAccountBalanceOnDelete(Account account, Transaction transaction) {
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

    private Account adjustAccountBalanceOnCreate(Account account, Transaction transaction) {
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

    private void checkTransactionAccessRights(Transaction transaction, User user) {
        if (!transaction.getAccount().getOwner().equals(user)) {
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
    }

    private void dateValidation(LocalDateTime startDate, LocalDateTime endDate){
        if (startDate.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Start date cannot be in the future");
        }

        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
    }
    public List<Transaction> getTransactionsByAccountAndDateRange(Account account, LocalDateTime startDate, LocalDateTime endDate) {
        dateValidation(startDate, endDate);
        List<Transaction> transactions = transactionRepository.findByAccountAndDateBetween(account, startDate, endDate);
        if (transactions.isEmpty()){
            throw new NotFoundException("No transactions found for this account during the specified period.");
        }
        return transactions;
    }
}
