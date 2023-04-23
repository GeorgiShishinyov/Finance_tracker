package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.BudgetDTOs.*;
import com.example.financetracker.model.DTOs.CategoryDTOs.CategoryDTO;
import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionDTO;
import com.example.financetracker.model.entities.Budget;
import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.entities.Currency;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService extends AbstractService{

    @Autowired
    private BudgetRepository budgetRepository;

    public BudgetDTO create(CreateBudgetDTO dto, int userId) {
        Budget budget = new Budget();
        Category category = getCategoryById(dto.getCategoryId());
        Currency currency = getCurrencyById(dto.getCurrencyId());
        budget.setStartDate(dto.getStartDate());
        budget.setEndDate(dto.getEndDate());
        budget.setBalance(dto.getBalance());
        budget.setCategory(category);
        budget.setCurrency(currency);
        budget.setDescription(dto.getDescription());
        budget.setOwner(getUserById(userId));
        validateBudgetInfo(budget);
        System.out.println(budget);
        budgetRepository.save(budget);
        logger.info("Created budget: "+budget.getId()+"\n"+budget.toString());

        return mapper.map(budget, BudgetDTO.class);
    }

    private void validateBudgetInfo(Budget budget){
        if(budget.getBalance().compareTo(BigDecimal.ZERO) < 0){
            throw new BadRequestException("Your balance can't be negative!");
        }
        if(budget.getStartDate().isAfter(budget.getEndDate())){
            throw new BadRequestException("End date can't be before start date!");
        }
    }

    public BudgetDTO edit(EditBudgetDTO dto, int id, int userId) {
        if(dto.getOwnerId() == userId) {
            Optional<Budget> budgetOptional = budgetRepository.findById(id);
            if(!budgetOptional.isPresent()){
                throw new NotFoundException("No such budget!");
            }
            Budget budget = budgetOptional.get();
            Category category = getCategoryById(dto.getCategoryId());
            Currency currency = getCurrencyById(dto.getCurrencyId());
            budget.setStartDate(dto.getStartDate());
            budget.setEndDate(dto.getEndDate());
            budget.setBalance(dto.getBalance());
            budget.setCategory(category);
            budget.setCurrency(currency);
            budget.setDescription(dto.getDescription());
            budget.setOwner(getUserById(userId));
            validateBudgetInfo(budget);
            budgetRepository.save(budget);
            logger.info("Updated budget: "+budget.getId()+"\n"+budget.toString());

            return mapper.map(budget, BudgetDTO.class);
        }
        throw new UnauthorizedException("You can not edit a budget on foreign profile!");
    }

    public BudgetDTO delete(int id, int userId) {
        Optional<Budget> optBudget = budgetRepository.findById(id);
        if(optBudget.isEmpty()){
            throw new NotFoundException("No such budget");
        }
        Budget budget = optBudget.get();
        if(budget.getOwner().getId() != userId){
            throw new UnauthorizedException("You can't delete foreign budget!");
        }
        budgetRepository.deleteById(id);
        logger.info("Deleted budget: "+budget.getId()+"\n"+budget.toString());

        return mapper.map(optBudget.get(), BudgetDTO.class);
    }

    public List<BudgetDTO> getAllBudgets(int userId) {
        List<Budget> budgets = budgetRepository.findAllByOwnerId(userId);
        return budgets.stream()
                .map(budget -> mapper.map(budget, BudgetDTO.class))
                .collect(Collectors.toList());
    }

    public BudgetWithTransactionsDTO getById(int id, int userId) {
        Optional<Budget> budgetOptional = budgetRepository.findById(id);
        if(!budgetOptional.isPresent()){
            throw new NotFoundException("No such budget");
        }
        Budget budget = budgetOptional.get();
        if(budget.getOwner().getId() != userId){
            throw new UnauthorizedException("You can't see foreign budget!");
        }
        List<TransactionDTO> transactionOnUser = new ArrayList<>();
        List<Transaction> transactions = transactionRepository.findAllByCategoryIdAndAccount_OwnerIdAndDateAfterAndDateBefore(
                budget.getOwner().getId(), budget.getCategory().getId(), budget.getStartDate(), budget.getEndDate());
        for(Transaction transaction : transactions){
            transactionOnUser.add(mapper.map(transaction, TransactionDTO.class));
        }

        BudgetWithTransactionsDTO budgetWithTransactionsDTO = new BudgetWithTransactionsDTO();
        budgetWithTransactionsDTO.setTransactions(transactionOnUser);
        budgetWithTransactionsDTO.setId(id);
        budgetWithTransactionsDTO.setBalance(budget.getBalance());
        budgetWithTransactionsDTO.setDescription(budget.getDescription());
        budgetWithTransactionsDTO.setEndDate(budget.getEndDate());
        budgetWithTransactionsDTO.setStartDate(budget.getStartDate());
        budgetWithTransactionsDTO.setCurrency(mapper.map(budget.getCurrency(), CurrencyDTO.class));
        budgetWithTransactionsDTO.setCategory(mapper.map(budget.getCategory(), CategoryDTO.class));

        return budgetWithTransactionsDTO;
    }
}
