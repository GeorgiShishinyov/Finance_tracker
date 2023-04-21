package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    List<Budget> findAllByOwnerId(int id);

    List<Budget> findBudgetByOwner_idAndCategory_idAndStartDateIsBeforeAndEndDateIsAfter(int owner_id, int category_id,
                                                                                         LocalDateTime dateOne, LocalDateTime dateTwo);
}
