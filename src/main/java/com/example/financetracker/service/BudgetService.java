package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.BudgetDTO;
import com.example.financetracker.model.DTOs.CreateBudgetDTO;
import com.example.financetracker.model.DTOs.DeleteBudgetDTO;
import com.example.financetracker.model.DTOs.EditBudgetDTO;
import com.example.financetracker.model.entities.Budget;
import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.entities.Currency;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        return mapper.map(budget, BudgetDTO.class);
    }

    private void validateBudgetInfo(Budget budget){
        if(budget.getBalance().compareTo(BigDecimal.ZERO) < 0){
            throw new BadRequestException("Your balance can't be negative!");
        }
        if(budget.getStartDate().isBefore(LocalDateTime.now())){
            throw new BadRequestException("You can not create a budget in the past!");
        }
        if(budget.getStartDate().isAfter(budget.getEndDate())){
            throw new BadRequestException("End date can't be before start date!");
        }
    }

    public EditBudgetDTO edit(EditBudgetDTO dto, int id, int userId) {
        if(dto.getOwnerId() == userId) {
            Optional<Budget> budgetOptional = budgetRepository.findById(id);
            if(!budgetOptional.isPresent()){
                throw new NotFoundException("No such budget!");
            }
            Budget budget = budgetOptional.get();
            Category category = getCategoryById(dto.getCategoryId());
            Currency currency = getCurrencyById(dto.getCurrencyId());
            System.out.println("start: " + dto.getStartDate());
            budget.setStartDate(dto.getStartDate());
            budget.setEndDate(dto.getEndDate());
            budget.setBalance(dto.getBalance());
            budget.setCategory(category);
            budget.setCurrency(currency);
            budget.setDescription(dto.getDescription());
            budget.setOwner(getUserById(userId));
            validateBudgetInfo(budget);
            budgetRepository.save(budget);
            return mapper.map(budget, EditBudgetDTO.class);
        }
        throw new UnauthorizedException("You can not edit a budget on foreign profile!");
    }

    public DeleteBudgetDTO delete(int id, int userId) {
        Optional<Budget> optBudget = budgetRepository.findById(id);
        if(optBudget.isEmpty()){
            throw new NotFoundException("No such budget");
        }
        Budget budget = optBudget.get();
        if(budget.getOwner().getId() != userId){
            throw new UnauthorizedException("You can't delete foreign budget!");
        }
        budgetRepository.deleteById(id);
        return mapper.map(optBudget.get(), DeleteBudgetDTO.class);
    }

    public List<CreateBudgetDTO> getAllBudgets(int userId) {
        List<Budget> budgets = budgetRepository.findAllByOwnerId(userId);
        return budgets.stream()
                .map(budget -> mapper.map(budget, CreateBudgetDTO.class))
                .collect(Collectors.toList());
    }

    public DeleteBudgetDTO getById(int id, int userId) {
        Optional<Budget> budgetOptional = budgetRepository.findById(id);
        if(!budgetOptional.isPresent()){
            throw new NotFoundException("No such budget");
        }
        Budget budget = budgetOptional.get();
        if(budget.getOwner().getId() != userId){
            throw new UnauthorizedException("You can't see foreign budget!");
        }
        return mapper.map(budget, DeleteBudgetDTO.class);
    }
}
