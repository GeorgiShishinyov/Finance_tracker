package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.TransferDTOs.TransferDTO;
import com.example.financetracker.model.DTOs.TransferDTOs.TransferRequestDTO;
import com.example.financetracker.service.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransferController extends AbstractController{

    @Autowired
    private TransferService transferService;

    @PostMapping("/transfers")
    public TransferDTO createTransfer(@Valid @RequestBody TransferRequestDTO transferRequestDTO, HttpSession s) {
        return transferService.createTransfer(getLoggedUserId(s), transferRequestDTO);
    }

    @GetMapping("/transfers/{id}")
    public TransferDTO getTransferById(@PathVariable int id, HttpSession s) {
        return transferService.getTransferById(id, getLoggedUserId(s));
    }
    /*
    @GetMapping("/transfers")
    public List<TransferDTO> getAllTransfersForUser(HttpSession s) {
        return transferService.getAllTransfersForUser(getLoggedUserId(s));
    }

     */

    @GetMapping("/transfers")
    public Page<TransferDTO> getAllTransfersForUser(HttpSession s,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return transferService.getAllTransfersForUser(getLoggedUserId(s), pageable);
    }

}
