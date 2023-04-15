package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {
}
