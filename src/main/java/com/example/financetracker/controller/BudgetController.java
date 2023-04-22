package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.BudgetDTOs.*;
import com.example.financetracker.service.BudgetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BudgetController extends AbstractController{

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/budgets")
    public BudgetDTO create(@Valid @RequestBody CreateBudgetDTO dto, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.create(dto, userId);
    }

    @PutMapping("budgets/{id}")
    public EditBudgetDTO edit(@Valid @PathVariable int id, @RequestBody EditBudgetDTO dto, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.edit(dto, id, userId);
    }

    @DeleteMapping("/budgets/{id}")
    public DeleteBudgetDTO delete(@PathVariable int id, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.delete(id, userId);
    }

    @GetMapping("/budgets")
    public List<CreateBudgetDTO> getAllBudgets(HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.getAllBudgets(userId);
    }

    @GetMapping("/budgets/{id}")
    public BudgetWithTransactionsDTO getById(@PathVariable int id, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.getById(id, userId);
    }
}
