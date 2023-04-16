package com.example.financetracker.service;

import com.example.financetracker.model.entities.*;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TransferRepository transferRepository;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected ModelMapper mapper;

    protected User getUserById(int id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    protected Transfer getTransferById(int id){
        return transferRepository.findById(id).orElseThrow(() -> new NotFoundException("Transfer not found"));
    }

    protected Account getAccountById(int id){
        return accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
    }

    protected Transaction getTransactionById(int id){
        return transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction not found"));
    }

    protected Category getCategoryById(int id){
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }


}
