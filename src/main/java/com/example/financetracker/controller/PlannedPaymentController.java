package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.PlannedPaymentDTOs.PlannedPaymentDTO;
import com.example.financetracker.model.DTOs.PlannedPaymentDTOs.PlannedPaymentRequestDTO;
import com.example.financetracker.model.DTOs.TransactionDTOs.TransactionDTOWithoutPlannedPayments;
import com.example.financetracker.service.PlannedPaymentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
public class PlannedPaymentController extends AbstractController{

    @Autowired
    private PlannedPaymentService plannedPaymentService;

    @PostMapping("/planned-payments")
    public PlannedPaymentDTO createPlannedPayment(@Valid @RequestBody PlannedPaymentRequestDTO plannedPaymentRequestDTO, HttpSession s) {
        return plannedPaymentService.createPlannedPayment(plannedPaymentRequestDTO, getLoggedUserId(s));
    }

    @PutMapping("/planned-payments/{id}")
    public PlannedPaymentDTO editPlannedPaymentById(@PathVariable int id, @RequestBody PlannedPaymentRequestDTO plannedPaymentRequestDTO, HttpSession s) {
        return plannedPaymentService.editPlannedPaymentById(id, plannedPaymentRequestDTO, getLoggedUserId(s));
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
    public Page<PlannedPaymentDTO> getAllPlannedPaymentsForAccount(@PathVariable int id, HttpSession s,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return plannedPaymentService.getAllPlannedPaymentsForAccount(id, getLoggedUserId(s), pageable);
    }

    @GetMapping("/planned-payments/{id}/transactions")
    public Page<TransactionDTOWithoutPlannedPayments> getAllTransactionsForPlannedPayment(@PathVariable int id, HttpSession s,
                                                                                          @RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return plannedPaymentService.getAllTransactionsForPlannedPayment(id, getLoggedUserId(s), pageable);
    }
}
