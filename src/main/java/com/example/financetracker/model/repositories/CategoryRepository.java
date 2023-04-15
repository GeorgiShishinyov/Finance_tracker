package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
