package com.example.financetracker.model.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EditAccountDTO extends CreateAccountDTO {
    
    private int id;
}
