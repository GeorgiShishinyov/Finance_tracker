package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Modifying
    @Query("UPDATE transactions SET date = ?1, amount = ?2, description = ?3, category = ?4 WHERE id = ?5")
    void editTransaction(LocalDateTime date, BigDecimal amount, String description, Category category, int id);
}
