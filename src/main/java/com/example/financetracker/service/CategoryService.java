package com.example.financetracker.service;

import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getCategoryById(int categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw new NotFoundException("Category not found!");
        }
    }

}
