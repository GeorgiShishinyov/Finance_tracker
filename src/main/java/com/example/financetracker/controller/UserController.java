package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.*;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/users/{id}")
    public UserFullInfoDTO editUserById(@PathVariable Integer id, @RequestBody UserEditDTO editDto, HttpSession s) {
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return userService.updateUserById(id, editDto);
    }

    @PutMapping("/users/{id}/password-change")
    public UserFullInfoDTO changePassword(@RequestBody UserPasswordChangeDTO passwordChangeDTO, @PathVariable Integer id, HttpSession s) {
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return userService.changePassword(id, passwordChangeDTO);
    }

    @DeleteMapping("/users/{id}")
    public UserFullInfoDTO deleteUserById(@PathVariable Integer id, HttpSession s) {
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return userService.deleteUserById(id);
    }
}
