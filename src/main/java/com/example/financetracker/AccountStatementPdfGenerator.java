package com.example.financetracker;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Transaction;
import com.example.financetracker.service.TransactionService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
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

    @SneakyThrows
    public ByteArrayOutputStream generatePdf(Account account, LocalDateTime startDate, LocalDateTime endDate, ByteArrayOutputStream outputStream)  {
        List<Transaction> transactions = transactionService.getTransactionsByAccountAndDateRange(account, startDate, endDate);

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        BigDecimal balance = account.getBalance();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        document.add(new Paragraph("Transactions for account: " + account.getName()));
        document.add(new Paragraph("Account owner: " + account.getOwner().getFirstName()+" "+account.getOwner().getLastName()));
        document.add(new Paragraph("Start Date: " + startDate.format(formatter)));
        document.add(new Paragraph("End Date: " + endDate.format(formatter)));
        document.add(new Paragraph("Starting balance: " + balance));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(5);
        PdfPCell dateHeader = new PdfPCell(new Phrase("Date"));
        dateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(dateHeader);

        PdfPCell categoryHeader = new PdfPCell(new Phrase("Category"));
        categoryHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(categoryHeader);

        PdfPCell descriptionHeader = new PdfPCell(new Phrase("Description"));
        descriptionHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(descriptionHeader);

        PdfPCell amountHeader = new PdfPCell(new Phrase("Amount"));
        amountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(amountHeader);

        PdfPCell balanceHeader = new PdfPCell(new Phrase("Balance"));
        balanceHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(balanceHeader);

        for (Transaction transaction : transactions) {
            table.addCell(formatter.format(transaction.getDate()));
            table.addCell(transaction.getCategory().getName());
            table.addCell(transaction.getDescription());
            table.addCell(transaction.getAmount().toString());
            table.addCell(balance.toString());

            balance = balance.add(transaction.getAmount());
        }

        document.add(table);

        document.close();
        return outputStream;
    }
}