package com.example.financetracker.service;

import com.example.financetracker.model.DTOs.AccountWithOwnerDTO;
import com.example.financetracker.model.DTOs.AccountWithoutOwnerDTO;
import com.example.financetracker.model.DTOs.CreateAccountDTO;
import com.example.financetracker.model.DTOs.EditAccountDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountService extends AbstractService{

    @Autowired
    private AccountRepository accountRepository;

    public AccountWithOwnerDTO create(CreateAccountDTO dto, int userId) {
        Account account = mapper.map(dto, Account.class);
        validateAccountData(account);
        User u = getUserById(userId);
        account.setOwner(u);
        accountRepository.save(account);
        return mapper.map(account, AccountWithOwnerDTO.class);
    }

    public AccountWithoutOwnerDTO edit(int id, EditAccountDTO dto, int userId) {
        Account account = mapper.map(dto, Account.class);
        account.setId(id);
        validateAccountData(account);
        if(account.getOwner().getId() != userId){
            throw new BadRequestException("You can not change the owner of account!");
        }
        accountRepository.save(account);
        return mapper.map(account, AccountWithoutOwnerDTO.class);
    }

    public AccountWithOwnerDTO getById(int id) {
        Optional<Account> account = accountRepository.findById(id);
        if(account.isPresent()){
            return mapper.map(account.get(), AccountWithOwnerDTO.class);
        }
        throw new NotFoundException("Account not found!");
    }

    private void validateAccountData(Account account){
        if(account.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("You cannot update your account balance with a negative or zero number value.");
        }
        if(account.getCurrency() == null){
            throw new BadRequestException("Please choose any currency for your account!");
        }
        if(account.getName() == null){
            throw new BadRequestException("You have to write any account name.");
        }
    }
}
