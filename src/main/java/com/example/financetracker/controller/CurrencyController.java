package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CurrencyController extends AbstractController{

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/currencies")
    public Page<CurrencyDTO> getAllCurrencies(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return currencyService.getAllCurrencies(pageable);
    }
}
