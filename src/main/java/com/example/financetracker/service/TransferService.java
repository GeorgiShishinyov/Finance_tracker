package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.DTOs.TransferRequestDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.TransferRepository;
import com.example.financetracker.model.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransferService extends AbstractService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    public TransferDTO getTransferById(int id, int userId ) {
        Optional<Transfer> optionalTransfer = transferRepository.findById(id);
        if (!optionalTransfer.isPresent()) {
            throw new NotFoundException("Transfer not found.");
        }
        if (optionalTransfer.get().getAccountSender().getOwner().getId() != userId){
            throw new UnauthorizedException("You don't have access to this transfer!");
        }
        return mapper.map(optionalTransfer.get(), TransferDTO.class);
    }

    @Transactional
    public TransferDTO createTransfer(int id, TransferRequestDTO transferRequestDTO) {
        User user = userRepository.findUserByIdAndAccountId(id, transferRequestDTO.getAccountSenderId());
        if(user == null){
            throw new UnauthorizedException("Permission denied. You do not have authorization to make this transfer.");
        }
        Account accountSender = accountRepository.findAccountById(transferRequestDTO.getAccountSenderId());
        if (accountSender == null) {
            throw new NotFoundException("Sender account not found.");
        }
        Account accountReceiver = accountRepository.findAccountById(transferRequestDTO.getAccountReceiverId());
        if (accountReceiver == null) {
            throw new NotFoundException("Receiver account not found.");
        }
        if (!accountReceiver.getOwner().equals(accountSender.getOwner())){
            throw new UnauthorizedException("Permission denied. You do not have authorization to make this transfer.");
        }
        if (accountSender.getBalance().compareTo(transferRequestDTO.getAmount()) <= 0) {
            throw new UnauthorizedException("Insufficient funds in sender account.");
        }
        Transfer transfer = new Transfer();
        transfer.setDate(LocalDateTime.now());
        transfer.setAccountSender(accountSender);
        transfer.setAccountReceiver(accountReceiver);
        transfer.setAmount(transferRequestDTO.getAmount());
        transfer.setDescription(transferRequestDTO.getDescription());
        transferRepository.save(transfer);
        accountSender.setBalance(accountSender.getBalance().subtract(transferRequestDTO.getAmount()));
        accountRepository.save(accountSender);
        //TODO Find API or @Bean for currency conversion.
        accountReceiver.setBalance(accountReceiver.getBalance().add(transferRequestDTO.getAmount()));
        accountRepository.save(accountReceiver);
        return mapper.map(transfer,TransferDTO.class);
    }
}
