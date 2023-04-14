package com.example.financetracker.service;

import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.NotFoundException;
import com.example.financetracker.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractService {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ModelMapper mapper;

    protected User getUserById(int id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
