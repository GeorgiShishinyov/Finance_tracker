package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.PlannedPaymentDTO;
import com.example.financetracker.model.DTOs.PlannedPaymentRequestDTO;
import com.example.financetracker.model.DTOs.TransactionDTO;
import com.example.financetracker.model.DTOs.TransactionRequestDTO;
import com.example.financetracker.model.entities.*;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.model.repositories.PlannedPaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class PlannedPaymentService extends AbstractService {

    @Autowired
    private TransactionService transactionService;

    public PlannedPaymentDTO createPlannedPayment(PlannedPaymentRequestDTO plannedPaymentRequestDTO, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Account account = getAccountById(plannedPaymentRequestDTO.getAccountId());
        checkAccountAccessRights(account, user);
        Category category = getCategoryById(plannedPaymentRequestDTO.getCategoryId());
        checkSufficientFunds(account.getBalance(), plannedPaymentRequestDTO.getAmount());
        PlannedPayment plannedPayment = new PlannedPayment();
        plannedPayment.setAccount(account);
        plannedPayment.setCategory(category);
        plannedPayment.setDescription(plannedPaymentRequestDTO.getDescription());
        plannedPayment.setAmount(plannedPaymentRequestDTO.getAmount());
        plannedPayment.setDate(plannedPaymentRequestDTO.getDate());
        plannedPayment.setFrequency(getFrequencyById(plannedPaymentRequestDTO.getFrequencyId()));
        plannedPaymentRepository.save(plannedPayment);
        return mapper.map(plannedPayment, PlannedPaymentDTO.class);
    }

    public PlannedPaymentDTO getPlannedPaymentById(int id, int loggedUserId) {
        User user = getUserById(loggedUserId);
        PlannedPayment plannedPayment = getPlannedPaymentById(id);
        checkAccountAccessRights(plannedPayment.getAccount(), user);
        return mapper.map(plannedPayment, PlannedPaymentDTO.class);
    }

    public PlannedPaymentDTO deletePlannedPaymentById(int plannedPaymentId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        PlannedPayment plannedPayment = getPlannedPaymentById(plannedPaymentId);
        checkAccountAccessRights(plannedPayment.getAccount(), user);
        List<Transaction> transactions = transactionRepository.findAllByPlannedPayment(plannedPayment);
        if (!transactions.isEmpty()) {
            throw new BadRequestException("Cannot delete planned payment that has related transactions.");
        }
        plannedPaymentRepository.delete(plannedPayment);
        return mapper.map(plannedPayment, PlannedPaymentDTO.class);
    }

    public List<PlannedPaymentDTO> getAllPlannedPaymentsForAccount(int accountId, int loggedUserId) {
        User user = getUserById(loggedUserId);
        Account account = getAccountById(accountId);
        checkAccountAccessRights(account, user);
        List<PlannedPayment> plannedPayments = plannedPaymentRepository.findAllByAccount(account);
        if (plannedPayments.isEmpty()) {
            throw new NotFoundException("Planned payments not found");
        }
        return plannedPayments.stream()
                .map(plannedPayment -> mapper.map(plannedPayment, PlannedPaymentDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    //@Scheduled(fixedDelay = 2000) // test - every 2 seconds
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000) // every 24 hours
    public void processPlannedPayments() {
        List<PlannedPayment> plannedPayments = plannedPaymentRepository.findAllByDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        for (PlannedPayment plannedPayment : plannedPayments) {
            TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
            transactionRequestDTO.setAmount(plannedPayment.getAmount());
            transactionRequestDTO.setDate(plannedPayment.getDate());
            transactionRequestDTO.setAccountId(plannedPayment.getAccount().getId());
            transactionRequestDTO.setDescription(plannedPayment.getDescription());
            transactionRequestDTO.setCategoryId(plannedPayment.getCategory().getId());
            transactionRequestDTO.setPlannedPaymentId(plannedPayment.getId());
            transactionService.createTransaction(transactionRequestDTO, plannedPayment.getAccount().getOwner().getId());

            Frequency frequency = plannedPayment.getFrequency();
            LocalDateTime nextPaymentDate = null;
            switch (frequency.getFrequency()) {
                case "WEEKLY":
                    nextPaymentDate = plannedPayment.getDate().plusWeeks(1);
                    break;
                case "MONTHLY":
                    nextPaymentDate = plannedPayment.getDate().plusMonths(1);
                    break;
                case "YEARLY":
                    nextPaymentDate = plannedPayment.getDate().plusYears(1);
                    break;
            }
            plannedPayment.setDate(nextPaymentDate);
        }
        plannedPaymentRepository.saveAll(plannedPayments);
    }

    private void checkAccountAccessRights(Account account, User user) {
        if (!account.getOwner().equals(user)) {
            throw new UnauthorizedException("Unauthorized access. The service cannot be executed.");
        }
    }

    private void checkSufficientFunds(BigDecimal balance, BigDecimal amount){
        if (balance.compareTo(amount) <= 0) {
            throw new UnauthorizedException("Insufficient funds in sender account.");
        }
    }

}
