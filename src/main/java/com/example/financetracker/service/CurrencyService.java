package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.model.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService extends AbstractService{

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<CurrencyDTO> getAllCurrencies() {
        return currencyRepository.findAll()
                .stream()
                .map(currency -> mapper.map(currency, CurrencyDTO.class))
                .collect(Collectors.toList());
    }

}
