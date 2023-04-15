package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
