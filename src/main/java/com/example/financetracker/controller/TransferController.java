package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.TransferDTOs.TransferDTO;
import com.example.financetracker.model.DTOs.TransferDTOs.TransferRequestDTO;
import com.example.financetracker.service.TransferService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransferController extends AbstractController{

    @Autowired
    private TransferService transferService;

    @GetMapping("/transfers/{id}")
    public TransferDTO getTransferById(@PathVariable int id, HttpSession s) {
        return transferService.getTransferById(id, getLoggedUserId(s));
    }
    @PostMapping("/transfers")
    public TransferDTO createTransfer(@RequestBody TransferRequestDTO transferRequestDTO, HttpSession s) {
        return transferService.createTransfer(getLoggedUserId(s), transferRequestDTO);
    }

    @GetMapping("/transfers")
    public List<TransferDTO> getAllTransfersForUser(HttpSession s) {
        return transferService.getAllTransfersForUser(getLoggedUserId(s));
    }

}
