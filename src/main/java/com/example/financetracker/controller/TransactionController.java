package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransactionEditRequestDTO;
import com.example.financetracker.model.DTOs.TransactionRequestDTO;
import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class TransactionController extends AbstractController {

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

    @GetMapping("/transactions/{id}")
    public TransactionDTO findTransactionById(@PathVariable int id, HttpSession s) {
        return transactionService.findTransactionById(id, getLoggedUserId(s));
    }

    @GetMapping("/users/{id}/transactions")
    public List<TransactionDTO> getAllTransactionsForUser(@PathVariable int id, HttpSession s) {
        return transactionService.getAllTransactionsForUser(id, getLoggedUserId(s));
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<TransactionDTO> getAllTransactionsForAccount(@PathVariable int id, HttpSession s) {
        return transactionService.getAllTransactionsForAccount(id, getLoggedUserId(s));
    }

    @GetMapping("/transactions/filter")
    public List<TransactionDTO> getFilteredTransactions(@RequestParam(name = "start-date")
                                                        @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
                                                        LocalDateTime startDate, @RequestParam(name = "end-date")
                                                        @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
                                                        LocalDateTime endDate, @RequestParam(name = "category-id") Integer categoryId,
                                                        @RequestParam(name = "account-id") Integer accountId, HttpSession s) {
        return transactionService.getFilteredTransactions(startDate, endDate, categoryId, accountId, getLoggedUserId(s));
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        registrar.registerFormatters((FormattingConversionService) binder.getConversionService());

        DateTimeFormatterRegistrar registrar2 = new DateTimeFormatterRegistrar();
        registrar2.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        registrar2.registerFormatters((FormattingConversionService) binder.getConversionService());
    }

}
