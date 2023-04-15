package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.DTOs.UserFullInfoDTO;
import com.example.financetracker.model.entities.PlannedPayment;
import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.PlannedPaymentRepository;
import com.example.financetracker.model.repositories.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransferService extends AbstractService{

    @Autowired
    private TransferRepository transferRepository;

    public TransferDTO getTransferById(int id) {
        Optional<Transfer> optionalTransfer = transferRepository.findById(id);
        if (!optionalTransfer.isPresent()) {
            throw new NotFoundException("Transfer not found.");
        }
        return mapper.map(optionalTransfer, TransferDTO.class);
    }
}
