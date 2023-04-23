package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.model.entities.Currency;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService extends AbstractService{

    @Autowired
    private CurrencyRepository currencyRepository;

    public Page<CurrencyDTO> getAllCurrencies(Pageable pageable) {
        Page<Currency> currencies = currencyRepository.findAll(pageable);
        if (currencies.isEmpty()) {
            throw new NotFoundException("No currencies found.");
        }
        return currencies.map(currency -> mapper.map(currency, CurrencyDTO.class));
    }

}
