package com.example.financetracker.service;

import com.example.financetracker.model.entities.Category;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class CategoryService extends AbstractService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found!"));
    }

    public Page<Category> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        if (categories.isEmpty()) {
            throw new NotFoundException("No categories found.");
        }
        return categories;
    }

    public List<Category> filterCategory(String name) {
        if(categoryRepository.findByName(name).size() != 0) {
            return categoryRepository.findByName(name);
        }else{
            throw new NotFoundException("Category not found");
        }
    }

    public File download(String fileName) {
        fileName = "uploads" + File.separator + fileName;
        File f = new File(fileName);
        System.out.println(fileName);
        if(f.exists()){
            return f;
        }
        throw new NotFoundException("File not found!");
    }
}
