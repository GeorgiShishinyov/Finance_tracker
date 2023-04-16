package com.example.financetracker.controller;

import com.example.financetracker.model.entities.PlannedPayment;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.PlannedPaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlannedPaymentController extends AbstractController{

    @Autowired
    private PlannedPaymentService plannedPaymentService;

    @GetMapping("/planned-payments/{id}")
    public PlannedPayment getPlannedPaymentById(@PathVariable int id, HttpSession s) {
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return plannedPaymentService.getPlannedPaymentById(id);
    }
}
