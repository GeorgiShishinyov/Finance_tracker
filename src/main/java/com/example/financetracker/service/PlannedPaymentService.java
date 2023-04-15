package com.example.financetracker.service;

import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.entities.PlannedPayment;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlannedPaymentService extends AbstractService {

    @Autowired
    private PlannedPaymentRepository plannedPaymentRepository;

    public PlannedPayment getPlannedPaymentById(int id) {
        return plannedPaymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Planned payment not found!"));
    }
}
