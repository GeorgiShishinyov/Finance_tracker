package com.example.financetracker.controller;

import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("categories/{id}")
    public Category getCategoryById(@PathVariable int id, HttpSession s) {
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return categoryService.getCategoryById(id);
    }
}
