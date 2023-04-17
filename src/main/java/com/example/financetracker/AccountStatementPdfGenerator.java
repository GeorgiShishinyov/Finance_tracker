package com.example.financetracker;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.service.TransactionService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AccountStatementPdfGenerator {

    @Autowired
    private TransactionService transactionService;

    public ByteArrayOutputStream generatePdf(Account account, LocalDateTime startDate, LocalDateTime endDate, ByteArrayOutputStream outputStream) throws Exception {
        List<Transaction> transactions = transactionService.getTransactionsByAccountAndDateRange(account, startDate, endDate);

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        BigDecimal balance = account.getBalance();

        document.add(new Paragraph("Transactions for account: " + account.getName()));
        document.add(new Paragraph("Account owner: " + account.getOwner().getFirstName()+" "+account.getOwner().getLastName()));
        document.add(new Paragraph("Start Date: " + startDate));
        document.add(new Paragraph("End Date: " + endDate));
        document.add(new Paragraph("Starting balance: " + balance));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(5);
        table.addCell("Date");
        table.addCell("Description");
        table.addCell("Category");
        table.addCell("Amount");
        table.addCell("Balance");

        for (Transaction transaction : transactions) {
            table.addCell(transaction.getDate().toString());
            table.addCell(transaction.getDescription());
            table.addCell(transaction.getCategory().getName());
            table.addCell(transaction.getAmount().toString());
            table.addCell(balance.toString());

            balance = balance.add(transaction.getAmount());
        }

        document.add(table);

        document.close();
        return outputStream;
    }
}