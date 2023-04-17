package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyExchangeDTO;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.service.AbstractService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.net.httpserver.Headers;
import lombok.SneakyThrows;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.ServerRequest;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.Scanner;


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
            return response.getBody();
        } else {
            throw new BadRequestException("Currency exchange API error.");
        }
    }
}