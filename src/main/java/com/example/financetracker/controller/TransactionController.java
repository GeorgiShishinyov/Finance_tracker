package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.DTOs.TransactionRequestDTO;
import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController extends AbstractController{

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/transactions")
    public TransactionDTO createTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO, HttpSession s) {
        return transactionService.createTransaction(transactionRequestDTO, getLoggedUserId(s));
    }

    @PutMapping("/transactions/{id}")
    public TransactionDTO editTransactionById(@PathVariable int id, @RequestBody TransactionEditRequestDTO transactionEditRequestDTO, HttpSession s) {
        return transactionService.editTransactionById(id, transactionEditRequestDTO, getLoggedUserId(s));
    }
    @DeleteMapping("/transactions/{id}")
    public TransactionDTO deleteTransactionById(@PathVariable int id, HttpSession s) {
        return transactionService.deleteTransactionById(id, getLoggedUserId(s));
    }

}
