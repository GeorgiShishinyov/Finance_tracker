package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrequencyRepository extends JpaRepository<Frequency, Integer> {
}
