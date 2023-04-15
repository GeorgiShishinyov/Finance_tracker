package com.example.financetracker.service;

import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService extends AbstractService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found!"));
    }

}
