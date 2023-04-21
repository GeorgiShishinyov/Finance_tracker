package com.example.financetracker.model.DTOs.ErrorDTOs;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDTO {

    private String message;
    private int status;
    private LocalDateTime time;
}
