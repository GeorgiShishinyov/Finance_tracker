package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyExchangeDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionRequestDTO;
import com.example.financetracker.model.entities.*;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.BudgetRepository;
import com.example.financetracker.model.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService extends AbstractService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    protected CurrencyExchangeService currencyExchangeService;

    @Transactional
    public TransactionDTO createTransaction(TransactionRequestDTO transactionRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Account account = getAccountById(transactionRequestDTO.getAccountId());
        authenticateUser(account.getOwner(), user);
        Category category = getCategoryById(transactionRequestDTO.getCategoryId());
        checkSufficientFunds(account.getBalance(), transactionRequestDTO.getAmount());
        Currency currency = getCurrencyById(transactionRequestDTO.getCurrencyId());
        BigDecimal amount = transactionRequestDTO.getAmount();
        amount = convertIfDifferentCurrency(currency.getId(), account.getCurrency().getId(), amount);
        Transaction transaction = new Transaction();
        transaction.setDate(transactionRequestDTO.getDate());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setDescription(transactionRequestDTO.getDescription());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setCurrency(account.getCurrency());
        // If the transaction request includes a planned payment ID, retrieve the corresponding planned payment
        Integer plannedPaymentId = transactionRequestDTO.getPlannedPaymentId();
        if (plannedPaymentId != null) {
            PlannedPayment plannedPayment = getPlannedPaymentById(plannedPaymentId);
            transaction.setPlannedPayment(plannedPayment);
        }
        account = adjustAccountBalanceOnCreate(account, transaction, amount);
        accountRepository.save(account);
        //TODO For refactoring - the logic needs to be refactored and extracted into a separate method - subtractAmountFromBudgets()
        if (transaction.getCategory().getType() == Category.CategoryType.EXPENSE) {
            List<Budget> budgets = budgetRepository.findBudgetByOwner_idAndCategory_idAndStartDateIsBeforeAndEndDateIsAfter(loggedUserId,
                    category.getId(), transaction.getDate(), transaction.getDate());
            if (budgets != null) {
                for (Budget budget : budgets) {
                    BigDecimal amountToSubtractFromBudget = transactionRequestDTO.getAmount();
                    if (budget.getCurrency().getId() != transaction.getCurrency().getId()) {
                        amountToSubtractFromBudget = convertCurrency(currency, budget.getCurrency(), amountToSubtractFromBudget);
                    }
                    budget = adjustBudgetBalanceOnCreate(budget, amountToSubtractFromBudget);
                    budgetRepository.save(budget);
                }
            }
        }
        transactionRepository.save(transaction);
        logger.info("Created transaction: " + transaction.getId() + "\n" + transaction.toString());

        return createTransactionDTO(currency, transaction);
    }

    @Transactional
    public TransactionDTO editTransactionById(int transactionId, TransactionEditRequestDTO transactionEditRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkUserAuthorization(transaction.getAccount().getOwner().getId(), user.getId());
        Account account = transaction.getAccount();
        Category category = getCategoryById(transactionEditRequestDTO.getCategoryId());
        Currency currency = getCurrencyById(transactionEditRequestDTO.getCurrencyId());
        BigDecimal originalAmount = transaction.getAmount();
        BigDecimal convertedAmount = originalAmount;
        convertedAmount = convertIfDifferentCurrency(transaction.getCurrency().getId(), account.getCurrency().getId(), convertedAmount);
        account = adjustAccountBalanceOnDelete(account, transaction, convertedAmount);
        accountRepository.save(account);
        //TODO For refactoring - the logic needs to be refactored and extracted into a separate method - subtractAmountFromBudgets()
        List<Budget> budgets = budgetRepository.findBudgetByOwner_idAndCategory_idAndStartDateIsBeforeAndEndDateIsAfter(loggedUserId,
                category.getId(), transaction.getDate(), transaction.getDate());
        if (budgets != null) {
            for (Budget budget : budgets) {
                BigDecimal amountToSubtractFromBudget = transaction.getAmount();
                if (budget.getCurrency().getId() != transaction.getCurrency().getId()) {
                    amountToSubtractFromBudget = convertCurrency(transaction.getCurrency(), budget.getCurrency(), amountToSubtractFromBudget);
                }
                budget = adjustBudgetBalanceOnDelete(budget, amountToSubtractFromBudget);
                budgetRepository.save(budget);
            }
        }
        transaction.setDate(transactionEditRequestDTO.getDate());
        transaction.setAmount(transactionEditRequestDTO.getAmount());
        transaction.setDescription(transactionEditRequestDTO.getDescription());
        transaction.setCategory(category);
        transaction.setCurrency(currency);
        BigDecimal newAmount = transactionEditRequestDTO.getAmount();
        BigDecimal convertedNewAmount = newAmount;
        convertedNewAmount = convertIfDifferentCurrency(currency.getId(), account.getCurrency().getId(), newAmount);
        if (transaction.getCategory().getType().equals(Category.CategoryType.INCOME)) {
            account.setBalance(account.getBalance().subtract(convertedAmount).add(convertedNewAmount));
        } else {
            checkSufficientFunds(account.getBalance().subtract(convertedAmount), convertedNewAmount);
            account.setBalance(account.getBalance().subtract(convertedNewAmount).add(convertedAmount));
        }
        accountRepository.save(account);
        //TODO For refactoring - the logic needs to be refactored and extracted into a separate method - subtractAmountFromBudgets()
        budgets = budgetRepository.findBudgetByOwner_idAndCategory_idAndStartDateIsBeforeAndEndDateIsAfter(loggedUserId,
                category.getId(), transaction.getDate(), transaction.getDate());
        if (budgets != null) {
            for (Budget budget : budgets) {
                BigDecimal amountToSubtractFromBudget = transaction.getAmount();
                if (budget.getCurrency().getId() != transaction.getCurrency().getId()) {
                    amountToSubtractFromBudget = convertCurrency(currency, budget.getCurrency(), amountToSubtractFromBudget);
                }
                budget = adjustBudgetBalanceOnCreate(budget, amountToSubtractFromBudget);
                budgetRepository.save(budget);
            }
        }
        transactionRepository.save(transaction);
        logger.info("Updated transaction: " + transaction.getId() + "\n" + transaction.toString());

        return createTransactionDTO(currency, transaction);
    }

    @Transactional
    public TransactionDTO deleteTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkUserAuthorization(transaction.getAccount().getOwner().getId(), user.getId());
        Account account = transaction.getAccount();
        BigDecimal originalAmount = transaction.getAmount();
        BigDecimal convertedAmount = originalAmount;
        convertedAmount = convertIfDifferentCurrency(transaction.getCurrency().getId(), account.getCurrency().getId(), originalAmount);
        account.setBalance(account.getBalance().add(convertedAmount));
        accountRepository.save(account);
        //TODO For refactoring - the logic needs to be refactored and extracted into a separate method - subtractAmountFromBudgets()
        List<Budget> budgets = budgetRepository.findBudgetByOwner_idAndCategory_idAndStartDateIsBeforeAndEndDateIsAfter(loggedUserId,
                transaction.getCategory().getId(), transaction.getDate(), transaction.getDate());
        if (budgets != null) {
            for (Budget budget : budgets) {
                BigDecimal amountToSubtractFromBudget = transaction.getAmount();
                if (budget.getCurrency().getId() != transaction.getCurrency().getId()) {
                    amountToSubtractFromBudget = convertCurrency(transaction.getCurrency(), budget.getCurrency(), amountToSubtractFromBudget);
                }
                budget = adjustBudgetBalanceOnDelete(budget, amountToSubtractFromBudget);
                budgetRepository.save(budget);
            }
        }

        transactionRepository.delete(transaction);
        logger.info("Deleted transaction: " + transaction.getId() + "\n" + transaction.toString());

        return createTransactionDTO(transaction.getCurrency(), transaction);
    }

    public TransactionDTO findTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkUserAuthorization(transaction.getAccount().getOwner().getId(), user.getId());
        return createTransactionDTO(transaction.getCurrency(), transaction);
    }

    @Transactional
    public List<TransactionDTO> getAllTransactionsForUser(int userId, int loggedUserId) {
        //TODO Implement pagination
        checkUserAuthorization(userId, loggedUserId);
        User user = getUserById(userId);
        List<Transaction> transactions = transactionRepository.findAllByAccount_Owner(user);
        checkIfTransactionsExist(transactions);

        return transactions.stream()
                .map(transaction -> createTransactionDTO(transaction.getCurrency(), transaction))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TransactionDTO> getAllTransactionsForAccount(int accountId, int loggedUserId) {
        //TODO Implement pagination
        User user = getUserById(loggedUserId);
        Account account = getAccountById(accountId);
        checkUserAuthorization(account.getOwner().getId(), user.getId());
        List<Transaction> transactions = transactionRepository.findAllByAccount(account);
        checkIfTransactionsExist(transactions);

        return transactions.stream()
                .map(transaction -> createTransactionDTO(transaction.getCurrency(), transaction))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getFilteredTransactions(LocalDateTime startDate, LocalDateTime endDate, Integer categoryId, Integer accountId, int loggedUserId) {
        //TODO Implement pagination
        User user = getUserById(loggedUserId);
        Account account = getAccountById(accountId);
        checkUserAuthorization(account.getOwner().getId(), user.getId());
        Category category = getCategoryById(categoryId);
        dateValidation(startDate, endDate);
        List<Transaction> transactions = transactionRepository.findByDateBetweenAndCategoryAndAccount(startDate, endDate, category, account);
        checkIfTransactionsExist(transactions);

        return transactions.stream()
                .map(transaction -> createTransactionDTO(transaction.getCurrency(), transaction))
                .collect(Collectors.toList());
    }

    private Account adjustAccountBalanceOnDelete(Account account, Transaction transaction, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance();
        Currency currency = transaction.getCurrency();
        amount = convertIfDifferentCurrency(currency.getId(), account.getCurrency().getId(), amount);
        if (transaction.getCategory().getType() == Category.CategoryType.INCOME) {
            newBalance = newBalance.subtract(amount);
        } else {
            newBalance = newBalance.add(amount);
        }
        account.setBalance(newBalance);

        return account;
    }

    private Account adjustAccountBalanceOnCreate(Account account, Transaction transaction, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance();
        Currency currency = transaction.getCurrency();
        amount = convertIfDifferentCurrency(currency.getId(), account.getCurrency().getId(), amount);
        if (transaction.getCategory().getType() == Category.CategoryType.INCOME) {
            newBalance = newBalance.add(amount);
        } else {
            newBalance = newBalance.subtract(amount);
        }
        account.setBalance(newBalance);

        return account;
    }

    private void dateValidation(LocalDateTime startDate, LocalDateTime endDate) {
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
        checkIfTransactionsExist(transactions);

        return transactions;
    }

    private Budget adjustBudgetBalanceOnCreate(Budget budget, BigDecimal amount) {
        //TODO comments reviewing
        //BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal newBalance = budget.getBalance();
        newBalance = newBalance.subtract(amount);
        budget.setBalance(newBalance);

        return budget;
    }

    private Budget adjustBudgetBalanceOnDelete(Budget budget, BigDecimal amount) {
        //TODO comments reviewing
        // BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal newBalance = budget.getBalance();
        newBalance = newBalance.add(amount);
        budget.setBalance(newBalance);

        return budget;
    }

    private BigDecimal convertIfDifferentCurrency(int currencyId, int currencyId2, BigDecimal amount) {
        if (currencyId != currencyId2) {
            amount = convertCurrency(getCurrencyById(currencyId), getCurrencyById(currencyId2), amount);
        }

        return amount;
    }

    private BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        CurrencyExchangeDTO dto =
                currencyExchangeService.getExchangedCurrency(fromCurrency.getKind(), toCurrency.getKind(), amount);

        return dto.getResult();
    }

    private TransactionDTO createTransactionDTO(Currency currency, Transaction transaction) {
        //TODO for reviewing and refactoring
        CurrencyDTO currencyDTO = mapper.map(currency, CurrencyDTO.class);
        TransactionDTO transactionDTO = mapper.map(transaction, TransactionDTO.class);
        transactionDTO.setCurrencyDTO(currencyDTO);

        return transactionDTO;
    }

    private void checkIfTransactionsExist(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            throw new NotFoundException("Transactions not found");
        }
    }
}
