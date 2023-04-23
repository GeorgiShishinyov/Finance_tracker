package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findAllByAccount_Owner(User user, Pageable pageable);

    Page<Transaction> findAllByAccount(Account account, Pageable pageable);

    Page<Transaction> findByDateBetweenAndCategoryAndAccount(LocalDateTime startDate, LocalDateTime endDate,
                                                             Category category, Account account, Pageable pageable);

    List<Transaction> findAllByPlannedPayment(PlannedPayment plannedPayment);

    Page<Transaction> findAllByPlannedPayment(PlannedPayment plannedPayment, Pageable pageable);

    List<Transaction> findByAccountAndDateBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findAllByCategoryIdAndAccount_OwnerIdAndDateAfterAndDateBefore(int categoryId, int ownerId, LocalDateTime start, LocalDateTime end);

    List<Transaction> findAllByAccount_IdAndDateBetween(int id, LocalDateTime startDate, LocalDateTime endDate);
}
