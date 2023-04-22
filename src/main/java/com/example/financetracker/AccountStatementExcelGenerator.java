package com.example.financetracker;

import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.service.TransactionService;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AccountStatementExcelGenerator {

    @Autowired
    private TransactionService transactionService;

    @SneakyThrows
    public ByteArrayOutputStream generateExcel(Account account, LocalDateTime startDate, LocalDateTime endDate, ByteArrayOutputStream outputStream) {

        List<Transaction> transactions = transactionService.getTransactionsByAccountAndDateRange(account, startDate, endDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        BigDecimal balance = account.getBalance();

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.createCell(1).setCellValue("Category");
        headerRow.createCell(2).setCellValue("Description");
        headerRow.createCell(3).setCellValue("Amount");
        headerRow.createCell(4).setCellValue("Currency");
        headerRow.createCell(5).setCellValue("Balance");

        int rowIndex = 1;
        for (Transaction transaction : transactions) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(transaction.getDate().toString());
            row.createCell(1).setCellValue(transaction.getCategory().getName());
            row.createCell(2).setCellValue(transaction.getDescription());
            row.createCell(3).setCellValue(transaction.getAmount().doubleValue());
            row.createCell(4).setCellValue(transaction.getCategory().getName());
            row.createCell(5).setCellValue(balance.doubleValue());

            balance = balance.add(transaction.getAmount());
        }

        // Autosize columns for better visibility
        for(int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new BadRequestException("Failed to write workbook to output stream.");
        }

        return outputStream;
    }
}
