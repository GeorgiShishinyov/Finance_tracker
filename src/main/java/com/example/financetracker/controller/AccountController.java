package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.AccountWithOwnerDTO;
import com.example.financetracker.model.DTOs.AccountWithoutOwnerDTO;
import com.example.financetracker.model.DTOs.CreateAccountDTO;
import com.example.financetracker.model.DTOs.EditAccountDTO;
import com.example.financetracker.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class AccountController extends AbstractController{

    @Autowired
    private AccountService accountService;

    @PostMapping("users/{id}/accounts")
    public AccountWithOwnerDTO create(@RequestBody CreateAccountDTO dto, HttpSession s){
        int id = getLoggedUserId(s);
        return accountService.create(dto, id);
    }

    @PutMapping("/accounts/{id}")
    public AccountWithoutOwnerDTO edit(@PathVariable int id, @RequestBody EditAccountDTO dto, HttpSession s){
        int userId = getLoggedUserId(s);
        return accountService.edit(id, dto, userId);
    }


}
