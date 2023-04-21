package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.UserDTOs.*;
import com.example.financetracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public UserFullInfoDTO register(@Valid @RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/users/login")
    public UserFullInfoDTO login(@Valid @RequestBody LoginDTO dto, HttpSession s, HttpServletRequest request) {
        UserFullInfoDTO respDto = userService.login(dto, request.getRemoteAddr());
        s.setAttribute("LOGGED_ID", respDto.getId());
        return respDto;
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession s) {
        // Check if the user is logged in
        getLoggedUserId(s);
        s.invalidate();
    }

    @PutMapping("/users/{id}")
    public UserFullInfoDTO editUserById(@PathVariable int id, @Valid @RequestBody UserEditDTO editDto, HttpSession s) {
        return userService.updateUserById(id, editDto, getLoggedUserId(s));
    }

    @DeleteMapping("/users/{id}")
    public UserFullInfoDTO deleteUserById(@PathVariable int id, HttpSession s) {
        return userService.deleteUserById(id, getLoggedUserId(s));
    }

    @GetMapping("/email-validation")
    public UserFullInfoDTO validateEmail(@RequestParam("code") String code) {
        return userService.validateCode(code);
    }

    @PutMapping("/users/{id}/password-change")
    public UserFullInfoDTO changePassword(@Valid @RequestBody UserPasswordChangeDTO passwordChangeDTO, @PathVariable int id, HttpSession s) {
        return userService.changePassword(id, passwordChangeDTO, getLoggedUserId(s));
    }

    @GetMapping("/users/{id}/invalidate")
    public ResponseEntity<String> invalidateSessions(@PathVariable Integer id, HttpServletRequest request) {
        return userService.invalidateSessions(id);
    }

}
