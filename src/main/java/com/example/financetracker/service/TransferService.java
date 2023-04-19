package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyExchangeDTO;
import com.example.financetracker.model.DTOs.TransferDTO;
import com.example.financetracker.model.DTOs.TransferRequestDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Currency;
import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.TransferRepository;
import com.example.financetracker.model.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferService extends AbstractService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

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
        if (accountSender.equals(accountReceiver)) {
            throw new BadRequestException("Transfer cannot be made to the same account");
        }
        if (accountSender.getBalance().compareTo(transferRequestDTO.getAmount()) <= 0) {
            throw new UnauthorizedException("Insufficient funds in sender account.");
        }

        accountSender.setBalance(accountSender.getBalance().subtract(transferRequestDTO.getAmount()));
        accountRepository.save(accountSender);
        BigDecimal amount = transferRequestDTO.getAmount();
        if (accountSender.getCurrency().getId() != accountReceiver.getCurrency().getId()) {
            amount = convertCurrency(accountSender.getCurrency(), accountReceiver.getCurrency(), amount);
        }
        accountReceiver.setBalance(accountReceiver.getBalance().add(amount));
        accountRepository.save(accountReceiver);
        Transfer transfer = new Transfer();
        transfer.setDate(LocalDateTime.now());
        transfer.setAccountSender(accountSender);
        transfer.setAccountReceiver(accountReceiver);
        transfer.setAmount(transferRequestDTO.getAmount());
        transfer.setDescription(transferRequestDTO.getDescription());
        transferRepository.save(transfer);
        logger.info("Created transfer: "+transfer.getId()+"\n"+transfer.toString());
        return mapper.map(transfer,TransferDTO.class);
    }

    private BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        CurrencyExchangeDTO dto =
                currencyExchangeService.getExchangedCurrency(fromCurrency.getKind(), toCurrency.getKind(), amount);
        return dto.getResult();
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
