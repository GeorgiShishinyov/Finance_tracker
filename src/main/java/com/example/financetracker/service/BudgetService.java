package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CreateBudgetDTO;
import com.example.financetracker.model.DTOs.EditBudgetDTO;
import com.example.financetracker.model.entities.Budget;
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

    public CreateBudgetDTO create(CreateBudgetDTO dto, int id, int userId) {
        if(id == userId) {
            Optional<Budget> optionalBudget = budgetRepository.findById(id);
            if(optionalBudget.isEmpty()){
                throw new NotFoundException("No such budget!");
            }
            System.out.println("a");
            Budget budget = mapper.map(dto, Budget.class);
            budget.setOwnerId(userId);
            System.out.println("b");
            validateCredentials(budget);
            budgetRepository.save(budget);
            return mapper.map(budget, CreateBudgetDTO.class);
        }
        throw new UnauthorizedException("You can not create a budget for foreign profile!");
    }

    private void validateCredentials(Budget budget){
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
            Budget budget = mapper.map(dto, Budget.class);
            budget.setOwnerId(userId);
            validateCredentials(budget);
            budgetRepository.save(budget);
            return mapper.map(budget, EditBudgetDTO.class);
        }
        throw new UnauthorizedException("You can not edit a budget on foreign profile!");
    }

    public EditBudgetDTO delete(int id, int userId) {
        Optional<Budget> optBudget = budgetRepository.findById(id);
        if(optBudget.isEmpty()){
            throw new NotFoundException("No such budget");
        }
        if(optBudget.get().getOwnerId() != userId){
            throw new UnauthorizedException("You can't delete foreign budget!");
        }
        budgetRepository.deleteById(id);
        return mapper.map(optBudget.get(), EditBudgetDTO.class);
    }

    public List<CreateBudgetDTO> getAllBudgets(int id, int userId) {
        List<Budget> budgets = budgetRepository.findAllByOwnerId(userId);
        return budgets.stream()
                .map(budget -> mapper.map(budget, CreateBudgetDTO.class))
                .collect(Collectors.toList());
    }
}
