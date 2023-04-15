package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlannedPaymentRepository extends JpaRepository<PlannedPayment, Integer> {

}
