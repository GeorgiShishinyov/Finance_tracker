package com.example.financetracker.controller;

import com.example.financetracker.model.DTOs.АccountDTOs.AccountWithOwnerDTO;
import com.example.financetracker.model.DTOs.АccountDTOs.AccountWithoutOwnerDTO;
import com.example.financetracker.model.DTOs.АccountDTOs.CreateAccountDTO;
import com.example.financetracker.model.DTOs.АccountDTOs.EditAccountDTO;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.model.exceptions.UnauthorizedException;
import com.example.financetracker.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class AccountController extends AbstractController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/users/{id}/accounts")
    public AccountWithOwnerDTO create(@PathVariable int id, @RequestBody CreateAccountDTO dto, HttpSession s) {
        getLoggedUserId(s);
        return accountService.create(dto, id);
    }

    @PutMapping("/accounts/{id}")
    public AccountWithoutOwnerDTO edit(@PathVariable int id, @RequestBody EditAccountDTO dto, HttpSession s) {
        int userId = getLoggedUserId(s);
        return accountService.edit(id, dto, userId);
    }

    @GetMapping("/accounts/{id}")
    public AccountWithOwnerDTO getById(@PathVariable int id, HttpSession s) {
        int userId = getLoggedUserId(s);
        return accountService.getById(id, userId);
    }

    @GetMapping("/accounts")
    public List<AccountWithoutOwnerDTO> getAllAccounts(HttpSession s) {
        int userId = getLoggedUserId(s);
        return accountService.getAllAccounts(userId);
    }

    @DeleteMapping("/accounts/{id}")
    public AccountWithoutOwnerDTO deleteAccountById(@PathVariable int id, HttpSession s) {
        int userId = getLoggedUserId(s);
        if (s.getAttribute("LOGGED") == null || !(Boolean) s.getAttribute("LOGGED")) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
        return accountService.deleteAccountById(id, userId);
    }


    @GetMapping("/accounts/{id}/export")
    public ResponseEntity<ByteArrayResource> exportAccountStatement(@PathVariable int id,
                                                                    @RequestParam(name = "format") String format,
                                                                    @RequestParam(name = "start-date")
                                                                    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                    LocalDateTime startDate,
                                                                    @RequestParam(name = "end-date")
                                                                    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                    LocalDateTime endDate,
                                                                    HttpSession session) {

        int userId = getLoggedUserId(session);
        ByteArrayOutputStream outputStream = null;
        String fileName = null;

        if ("pdf".equalsIgnoreCase(format)) {
            outputStream = accountService.generateAccountStatementPdf(id, startDate, endDate, userId);
            fileName = "statement.pdf";
        } else if ("xlsx".equalsIgnoreCase(format)) {
            outputStream = accountService.generateAccountStatementExcel(id, startDate, endDate, userId);
            fileName = "statement.xlsx";
        } else {
            throw new BadRequestException("Unsupported format: " + format);
        }

        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
