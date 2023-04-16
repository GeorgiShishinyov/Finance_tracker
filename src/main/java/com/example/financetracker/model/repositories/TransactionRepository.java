package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Modifying
    @Query("UPDATE transactions SET date = ?1, amount = ?2, description = ?3, category = ?4 WHERE id = ?5")
    void editTransaction(LocalDateTime date, BigDecimal amount, String description, Category category, int id);

    List<Transaction> findAllByAccount_Owner(User user);

    List<Transaction> findAllByAccount(Account account);

    List<Transaction> findByDateBetweenAndCategoryAndAccount(LocalDateTime startDate, LocalDateTime endDate, Category category, Account account);
}
