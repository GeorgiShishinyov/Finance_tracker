package com.example.financetracker.service;

import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.TransactionRepository;
import com.example.financetracker.model.repositories.TransferRepository;
import com.example.financetracker.model.repositories.UserRepository;
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


}
