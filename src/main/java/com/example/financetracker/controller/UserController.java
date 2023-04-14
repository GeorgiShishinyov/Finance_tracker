package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.LoginDTO;
import com.example.financetracker.model.DTOs.RegisterDTO;
import com.example.financetracker.model.DTOs.UserFullInfoDTO;
import com.example.financetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public UserFullInfoDTO register(@RequestBody RegisterDTO dto){
        return userService.register(dto);
    }

    @PostMapping("/users/login")
    public UserFullInfoDTO login(@RequestBody LoginDTO dto, HttpSession s){
        UserFullInfoDTO respDto = userService.login(dto);
        s.setAttribute("LOGGED", true);
        s.setAttribute("LOGGED_ID", respDto.getId());
        return respDto;
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession s) {
        s.invalidate();
    }
}
