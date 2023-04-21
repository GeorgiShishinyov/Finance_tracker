package com.example.financetracker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.financetracker.model.DTOs.BudgetDTOs.CreateBudgetDTO;
import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.CategoryRepository;
import com.example.financetracker.model.repositories.CurrencyRepository;
import com.example.financetracker.service.BudgetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void createBudgetThrowsNotFoundExceptionWhenInvalidCategory() {
        // arrange
        CreateBudgetDTO createDto = new CreateBudgetDTO("Budget Description",
                LocalDateTime.of(2023, 4, 1, 0, 0, 0),
                LocalDateTime.of(2023, 4, 30, 0, 0, 0),
                new BigDecimal("2000"),
                1,
                2);
        int userId = 1;
        when(categoryRepository.findById(createDto.getCategoryId())).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> budgetService.create(createDto, userId));
    }

    @Test
    void createBudgetThrowsNotFoundExceptionWhenInvalidCurrency() {
        // arrange
        CreateBudgetDTO createDto = new CreateBudgetDTO("Budget Description",
                LocalDateTime.of(2023, 4, 1, 0, 0, 0),
                LocalDateTime.of(2023, 4, 30, 0, 0, 0),
                new BigDecimal("2000"),
                1,
                2);
        int userId = 1;
        Category category = new Category();
        when(categoryRepository.findById(createDto.getCategoryId())).thenReturn(Optional.of(category));
        when(currencyRepository.findById(createDto.getCurrencyId())).thenReturn(Optional.empty());

        // act & assert
        assertThrows(NotFoundException.class, () -> budgetService.create(createDto, userId));
    }
}


