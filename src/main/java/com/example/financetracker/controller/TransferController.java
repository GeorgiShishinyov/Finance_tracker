package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.DTOs.TransferRequestDTO;
import com.example.financetracker.model.entities.PlannedPayment;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.PlannedPaymentService;
import com.example.financetracker.service.TransferService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransferController extends AbstractController{

    @Autowired
    private TransferService transferService;

    @GetMapping("/transfers/{id}")
    public TransferDTO getTransferById(@PathVariable int id, HttpSession s) {
        return transferService.getTransferById(id, getLoggedUserId(s));
    }
    @PostMapping("/transfers")
    public TransferDTO createTransfer(@RequestBody TransferRequestDTO transferRequestDTO, HttpSession s) {
        return transferService.createTransfer(getLoggedUserId(s), transferRequestDTO);
    }
}
