package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyExchangeDTO;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


@Service
public class CurrencyExchangeService extends AbstractService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public CurrencyExchangeDTO getExchangedCurrency(String from, String to, BigDecimal amount) {
        String url = "https://api.apilayer.com/exchangerates_data/convert?to=" + to + "&from=" + from + "&amount=" + amount;
        HttpHeaders headers = new HttpHeaders();
        String key = "XPCCY4FCPoiGzqgh4tMo405dTeFF7kTd";
        headers.set("apikey", key);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CurrencyExchangeDTO> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, CurrencyExchangeDTO.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Currency exchange made: " + "\n" +
                    "Amount: " + amount + "\n" +
                    "From: " + from.toString() + "\n" + 
                    "To: " + to.toString());

            return response.getBody();
        } else {
            throw new BadRequestException("Currency exchange API error.");
        }
    }
}