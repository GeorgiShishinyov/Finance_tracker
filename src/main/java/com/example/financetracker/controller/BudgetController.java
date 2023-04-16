package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.AccountWithOwnerDTO;
import com.example.financetracker.model.DTOs.CreateAccountDTO;
import com.example.financetracker.model.DTOs.CreateBudgetDTO;
import com.example.financetracker.model.DTOs.EditBudgetDTO;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.service.BudgetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BudgetController extends AbstractController{

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/users/{id}/budgets")
    public CreateBudgetDTO create(@PathVariable int id, @RequestBody CreateBudgetDTO dto, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.create(dto, id, userId);
    }

    @PutMapping("budgets/{id}")
    public EditBudgetDTO edit(@PathVariable int id, @RequestBody EditBudgetDTO dto, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.edit(dto, id, userId);
    }

    @DeleteMapping("budgets/{id}")
    public EditBudgetDTO delete(@PathVariable int id, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.delete(id, userId);
    }

    @GetMapping("users/{id}/budgets")
    public List<CreateBudgetDTO> getAllBudgets(@PathVariable int id, HttpSession s){
        int userId = getLoggedUserId(s);
        return budgetService.getAllBudgets(id, userId);
    }
}
