package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.PlannedPaymentDTOs.PlannedPaymentDTO;
import com.example.financetracker.model.DTOs.PlannedPaymentDTOs.PlannedPaymentRequestDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionDTOWithoutPlannedPayments;
import com.example.financetracker.service.PlannedPaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlannedPaymentController extends AbstractController{

    @Autowired
    private PlannedPaymentService plannedPaymentService;

    @PostMapping("/planned-payments")
    public PlannedPaymentDTO createPlannedPayment(@RequestBody PlannedPaymentRequestDTO plannedPaymentRequestDTO, HttpSession s) {
        return plannedPaymentService.createPlannedPayment(plannedPaymentRequestDTO, getLoggedUserId(s));
    }

    @GetMapping("/planned-payments/{id}")
    public PlannedPaymentDTO getPlannedPaymentById(@PathVariable int id, HttpSession s) {
        return plannedPaymentService.getPlannedPaymentById(id, getLoggedUserId(s));
    }

    @DeleteMapping("/planned-payments/{id}")
    public PlannedPaymentDTO deletePlannedPaymentById(@PathVariable int id, HttpSession s) {
        return plannedPaymentService.deletePlannedPaymentById(id, getLoggedUserId(s));
    }

    @GetMapping("/accounts/{id}/planned-payments")
    public List<PlannedPaymentDTO> getAllPlannedPaymentsForAccount(@PathVariable int id, HttpSession s) {
        return plannedPaymentService.getAllPlannedPaymentsForAccount(id, getLoggedUserId(s));
    }

    @GetMapping("/planned-payments/{id}/transactions")
    public List<TransactionDTOWithoutPlannedPayments> getAllTransactionsForPlannedPayment(@PathVariable int id, HttpSession s) {
        return plannedPaymentService.getAllTransactionsForPlannedPayment(id, getLoggedUserId(s));
    }

    @PutMapping("/planned-payments/{id}")
    public PlannedPaymentDTO editPlannedPaymentById(@PathVariable int id, @RequestBody PlannedPaymentRequestDTO plannedPaymentRequestDTO, HttpSession s) {
        return plannedPaymentService.editPlannedPaymentById(id, plannedPaymentRequestDTO, getLoggedUserId(s));
    }
}
