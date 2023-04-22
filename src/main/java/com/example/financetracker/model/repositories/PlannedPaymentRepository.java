package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.PlannedPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlannedPaymentRepository extends JpaRepository<PlannedPayment, Integer> {

    List<PlannedPayment> findAllByDate(LocalDateTime localDateTime);

    Page<PlannedPayment> findAllByAccount(Account account, Pageable pageable);
}
