package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    Page<Currency> findAll(Pageable pageable);
}
