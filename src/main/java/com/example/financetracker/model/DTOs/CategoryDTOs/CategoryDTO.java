package com.example.financetracker.model.DTOs.CategoryDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private int id;
    private String iconUrl;
    private String name;
    private String type;

}
