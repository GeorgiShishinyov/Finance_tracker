package com.example.financetracker.service;

import com.example.financetracker.ExchangeRatesAPIClient;
import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.DTOs.TransferRequestDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Currency;
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransferService extends AbstractService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExchangeRatesAPIClient exchangeRatesAPIClient;

    public TransferDTO getTransferById(int id, int userId ) {
        Transfer transfer = getTransferById(id);
        if (transfer.getAccountSender().getOwner().getId() != userId){
            throw new UnauthorizedException("You don't have access to this transfer!");
        }
        return mapper.map(transfer, TransferDTO.class);
    }

    @Transactional
    public TransferDTO createTransfer(int id, TransferRequestDTO transferRequestDTO) {
        User user = userRepository.findUserByIdAndAccountId(id, transferRequestDTO.getAccountSenderId());
        if(user == null){
            throw new UnauthorizedException("Permission denied. You do not have authorization to make this transfer.");
        }
        Account accountSender = getAccountById(transferRequestDTO.getAccountSenderId());
        Account accountReceiver = getAccountById(transferRequestDTO.getAccountReceiverId());
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
        //Get the exchange rate for the currency conversion
        Map<Currency, BigDecimal> exchangeRates = exchangeRatesAPIClient.getExchangeRates(accountSender.getCurrency(), accountReceiver.getCurrency());
        BigDecimal exchangeRate = exchangeRates.get(accountReceiver.getCurrency()).divide(exchangeRates.get(accountSender.getCurrency()), MathContext.DECIMAL32);
        BigDecimal convertedAmount = transferRequestDTO.getAmount().multiply(exchangeRate);
        accountReceiver.setBalance(accountReceiver.getBalance().add(convertedAmount));
        accountRepository.save(accountReceiver);
        return mapper.map(transfer,TransferDTO.class);
    }

    public List<TransferDTO> getAllTransfersForUser(int loggedUserId) {
        List<Transfer> transfers = transferRepository.findByAccountSender_Owner_Id(loggedUserId);
        if (transfers.isEmpty()){
            throw new NotFoundException("No transfers found for the user.");
        }
        List<TransferDTO> transferDTOs = new ArrayList<>();
        for (Transfer transfer : transfers) {
            transferDTOs.add(mapper.map(transfer, TransferDTO.class));
        }
        return transferDTOs;
    }
}
