package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    List<Budget> findAllByOwnerId(int id);

    List<Budget> findBudgetByOwnerIdAndCategoryIdOrderByBalanceDesc(int ownerId, int currencyId);

    List<Budget> findBudgetByOwnerIdAndCategoryIdOrderByBalance(int ownerId, int currencyId);
}
