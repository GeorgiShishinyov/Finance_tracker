package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyExchangeDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionRequestDTO;
import com.example.financetracker.model.entities.*;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
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
        if (!account.getOwner().equals(user)) {
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
        Category category = getCategoryById(transactionRequestDTO.getCategoryId());
        if (account.getBalance().compareTo(transactionRequestDTO.getAmount()) < 0) {
            throw new UnauthorizedException("Insufficient funds in sender account.");
        }

        Currency currency = getCurrencyById(transactionRequestDTO.getCurrencyId());
        BigDecimal amount = transactionRequestDTO.getAmount();
        if (currency.getId() != account.getCurrency().getId()) {
            amount = convertCurrency(currency, account.getCurrency(), amount);
        }

        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setDescription(transactionRequestDTO.getDescription());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setCurrency(account.getCurrency());
        Integer plannedPaymentId = transactionRequestDTO.getPlannedPaymentId();
        if(plannedPaymentId != null){
            PlannedPayment plannedPayment = getPlannedPaymentById(plannedPaymentId);
            transaction.setPlannedPayment(plannedPayment);
        }
        account = adjustAccountBalanceOnCreate(account, transaction, amount);
        accountRepository.save(account);
        //TODO Georgi add start - end day and remove order by ->
        if(transaction.getCategory().getType() == Category.CategoryType.EXPENSE) {
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
        logger.info("Created transaction: "+transaction.getId()+"\n"+transaction.toString());
        CurrencyDTO currencyDTO = mapper.map(currency, CurrencyDTO.class);
        TransactionDTO transactionDTO = mapper.map(transaction, TransactionDTO.class);
        transactionDTO.setCurrencyDTO(currencyDTO);
        return transactionDTO;
    }

    @Transactional
    public TransactionDTO deleteTransactionById(int transactionId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkTransactionAccessRights(transaction, user);
        Account account = transaction.getAccount();
        BigDecimal originalAmount = transaction.getAmount();
        BigDecimal convertedAmount = originalAmount;
        if (transaction.getCurrency().getId() != account.getCurrency().getId()) {
            convertedAmount = convertCurrency(transaction.getCurrency(), account.getCurrency(), originalAmount);
        }
        account.setBalance(account.getBalance().add(convertedAmount));
        accountRepository.save(account);

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
        logger.info("Deleted transaction: "+transaction.getId()+"\n"+transaction.toString());

        return mapper.map(transaction, TransactionDTO.class);
    }


    @Transactional
    public TransactionDTO editTransactionById(int transactionId, TransactionEditRequestDTO transactionEditRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Transaction transaction = getTransactionById(transactionId);
        checkTransactionAccessRights(transaction, user);
        Account account = transaction.getAccount();
        Category category = getCategoryById(transactionEditRequestDTO.getCategoryId());
        Currency currency = getCurrencyById(transactionEditRequestDTO.getCurrencyId());
        BigDecimal originalAmount = transaction.getAmount();
        BigDecimal convertedAmount = originalAmount;
        if (transaction.getCurrency().getId() != account.getCurrency().getId()) {
            convertedAmount = convertCurrency(transaction.getCurrency(), account.getCurrency(), originalAmount);
        }
        account = adjustAccountBalanceOnDelete(account, transaction, convertedAmount);
        accountRepository.save(account);
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
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(transactionEditRequestDTO.getAmount());
        transaction.setDescription(transactionEditRequestDTO.getDescription());
        transaction.setCategory(category);
        transaction.setCurrency(currency);
        BigDecimal newAmount = transactionEditRequestDTO.getAmount();
        BigDecimal convertedNewAmount = newAmount;
        if (currency.getId() != account.getCurrency().getId()) {
            convertedNewAmount = convertCurrency(currency, account.getCurrency(), newAmount);
        }
        if (transaction.getCategory().getType() == Category.CategoryType.INCOME) {
            account.setBalance(account.getBalance().subtract(convertedAmount).add(convertedNewAmount));
        } else {
            if (account.getBalance().subtract(convertedAmount).compareTo(convertedNewAmount) < 0) {
                throw new UnauthorizedException("Insufficient funds in sender account.");
            }
            account.setBalance(account.getBalance().subtract(convertedNewAmount).add(convertedAmount));
        }
        accountRepository.save(account);
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
        logger.info("Updated transaction: "+transaction.getId()+"\n"+transaction.toString());
        CurrencyDTO currencyDTO = mapper.map(currency, CurrencyDTO.class);
        TransactionDTO transactionDTO = mapper.map(transaction, TransactionDTO.class);
        transactionDTO.setCurrencyDTO(currencyDTO);
        return transactionDTO;
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

    private Account adjustAccountBalanceOnDelete(Account account, Transaction transaction, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance();
        Currency currency = transaction.getCurrency();
        if (currency.getId() != account.getCurrency().getId()) {
            amount = convertCurrency(currency, account.getCurrency(), amount);
        }
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
        if (currency.getId() != account.getCurrency().getId()) {
            amount = convertCurrency(currency, account.getCurrency(), amount);
        }
        if (transaction.getCategory().getType() == Category.CategoryType.INCOME) {
            newBalance = newBalance.add(amount);
        } else {
            newBalance = newBalance.subtract(amount);
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

    private BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        CurrencyExchangeDTO dto =
                currencyExchangeService.getExchangedCurrency(fromCurrency.getKind(), toCurrency.getKind(), amount);
        return dto.getResult();
    }

    private Budget adjustBudgetBalanceOnCreate(Budget budget, BigDecimal amount) {
//        BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal newBalance = budget.getBalance();
        newBalance = newBalance.subtract(amount);
        budget.setBalance(newBalance);
        return budget;
    }

    private Budget adjustBudgetBalanceOnDelete(Budget budget, BigDecimal amount) {
//        BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal newBalance = budget.getBalance();
        newBalance = newBalance.add(amount);
        budget.setBalance(newBalance);
        return budget;
    }

}
