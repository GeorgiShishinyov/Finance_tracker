package com.example.financetracker.service;

import com.example.financetracker.model.entities.*;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public abstract class AbstractService {

    @Autowired
    protected CurrencyRepository currencyRepository;

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
    protected FrequencyRepository frequencyRepository;

    @Autowired
    protected PlannedPaymentRepository plannedPaymentRepository;

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

    protected Frequency getFrequencyById(int id){
        return frequencyRepository.findById(id).orElseThrow(() -> new NotFoundException("Frequency not found"));
    }

    protected PlannedPayment getPlannedPaymentById(int id){
        return plannedPaymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Planned payment not found"));
    }

    protected Currency getCurrencyById(int id){
        Optional<Currency> currency = currencyRepository.findById(id);
        if(currency.isPresent()){
            return currency.get();
        }
        throw new NotFoundException("No such currency!");
    }

}
