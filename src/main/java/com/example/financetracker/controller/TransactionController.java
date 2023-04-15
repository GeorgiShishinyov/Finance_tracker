package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController extends AbstractController{

    @Autowired
    private TransactionService transactionService;

    @DeleteMapping("/transactions/{id}")
    public TransactionDTO deleteTransactionById(@PathVariable int id, HttpSession s) {
        return transactionService.deleteTransactionById(id, getLoggedUserId(s));
    }

}
