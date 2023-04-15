package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.*;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class AccountController extends AbstractController{

    @Autowired
    private AccountService accountService;

    @PostMapping("/users/{id}/accounts")
    public AccountWithOwnerDTO create(@PathVariable int id,@RequestBody CreateAccountDTO dto, HttpSession s){
        getLoggedUserId(s);
        return accountService.create(dto, id);
    }

    @PutMapping("/accounts/{id}")
    public AccountWithoutOwnerDTO edit(@PathVariable int id, @RequestBody EditAccountDTO dto, HttpSession s){
        int userId = getLoggedUserId(s);
        return accountService.edit(id, dto, userId);
    }

    @GetMapping("/accounts/{id}")
    public AccountWithOwnerDTO getById(@PathVariable int id, HttpSession s){
        getLoggedUserId(s);
        return accountService.getById(id);
    }

    @GetMapping("/users/{id}/accounts")
    public List<AccountWithoutOwnerDTO> getAllAccounts(@PathVariable int id, HttpSession s){
        getLoggedUserId(s);
        return accountService.getAllAccounts(id);
    }

    @DeleteMapping("/accounts/{id}")
    public AccountWithoutOwnerDTO deleteAccountById(@PathVariable int id, HttpSession s) {
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return accountService.deleteAccountById(id);
    }


}
