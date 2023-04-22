package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyExchangeDTO;
import com.example.financetracker.model.DTOs.TransferDTOs.TransferDTO;
import com.example.financetracker.model.DTOs.TransferDTOs.TransferRequestDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Currency;
import com.example.financetracker.model.entities.Transfer;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.TransferRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService extends AbstractService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    protected CurrencyExchangeService currencyExchangeService;

    @Transactional
    public TransferDTO createTransfer(int loggedUserId, TransferRequestDTO transferRequestDTO) {
        User user = userRepository.findUserByIdAndAccountId(loggedUserId, transferRequestDTO.getAccountSenderId());
        if (user == null) {
            throw new UnauthorizedException("Permission denied. You do not have authorization to make this transfer.");
        }
        Account accountSender = getAccountById(transferRequestDTO.getAccountSenderId());
        Account accountReceiver = getAccountById(transferRequestDTO.getAccountReceiverId());
        checkTransferAuthorizationByAccountOwners(accountReceiver.getOwner(), accountSender.getOwner());
        checkTransferDestinationIsDifferentAccount(accountSender, accountReceiver);
        checkSufficientFunds(accountSender.getBalance(), transferRequestDTO.getAmount());
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
        logger.info("Created transfer: " + transfer.getId() + "\n" + transfer.toString());

        return mapper.map(transfer, TransferDTO.class);
    }

    public TransferDTO getTransferById(int id, int loggedUserId) {
        Transfer transfer = transferRepository.findById(id).orElseThrow(() -> new NotFoundException("Transfer not found"));
        checkUserAuthorization(transfer.getAccountSender().getOwner().getId(), loggedUserId);

        return mapper.map(transfer, TransferDTO.class);
    }

    public Page<TransferDTO> getAllTransfersForUser(int loggedUserId, Pageable pageable) {
        Page<Transfer> transfers = transferRepository.findAllByAccountSender_Owner_Id(loggedUserId, pageable);
        if (transfers.isEmpty()) {
            throw new NotFoundException("No transfers found for the user.");
        }
        Page<TransferDTO> transferDTOs = transfers.map(transfer -> mapper.map(transfer, TransferDTO.class));

        return transferDTOs;
    }

    private BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        CurrencyExchangeDTO dto =
                currencyExchangeService.getExchangedCurrency(fromCurrency.getKind(), toCurrency.getKind(), amount);

        return dto.getResult();
    }

    private void checkTransferAuthorizationByAccountOwners(User sender, User receiver) {
        //check if accounts belong to the same owner
        if (!sender.equals(receiver)) {
            throw new UnauthorizedException("Permission denied. You do not have authorization to make this transfer.");
        }
    }

    private void checkTransferDestinationIsDifferentAccount(Account accountSender, Account accountReceiver) {
        if (accountSender.equals(accountReceiver)) {
            throw new BadRequestException("Transfer cannot be made to the same account");
        }
    }

}
