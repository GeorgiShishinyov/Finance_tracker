package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CurrencyController extends AbstractController{

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/currencies")
    public List<CurrencyDTO> getAllCurrencies(){
        return currencyService.getAllCurrencies();
    }
}
