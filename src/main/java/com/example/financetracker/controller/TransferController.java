package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.entities.PlannedPayment;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.PlannedPaymentService;
import com.example.financetracker.service.TransferService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController extends AbstractController{

    @Autowired
    private TransferService transferService;

    @GetMapping("/transfers/{id}")
    public TransferDTO getTransferById(@PathVariable int id, HttpSession s) {
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return transferService.getTransferById(id);
    }
}
