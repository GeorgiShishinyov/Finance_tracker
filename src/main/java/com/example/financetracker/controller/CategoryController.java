package com.example.financetracker.controller;

import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.CategoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@RestController
public class CategoryController extends AbstractController{

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories/{id}")
    public Category getCategoryById(@PathVariable int id, HttpSession s) {
        getLoggedUserId(s);
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/categories/filter")
    public List<Category> filterCategory(@RequestParam ("name") @NotNull @NotBlank(message = "Filter can not be empty!")
                                             @Size(max = 15) String name, HttpSession s){
        getLoggedUserId(s);
        return categoryService.filterCategory(name);
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories(HttpSession s){
        getLoggedUserId(s);
        return categoryService.getAllCategories();
    }

    @SneakyThrows
    @GetMapping("/categories/{id}/image")
    public void download(@PathVariable int id, HttpServletResponse response, HttpSession s){
        getLoggedUserId(s);
        Category category = categoryService.getCategoryById(id);
        File f = categoryService.download(category.getIconUrl());
        Files.copy(f.toPath(), response.getOutputStream());
    }
}