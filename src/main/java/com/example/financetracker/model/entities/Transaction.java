package com.example.financetracker.model.entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Expose
    @Column(name = "description")
    private String description;

    @Expose
    @Column(name = "date")
    private LocalDateTime date;

    @Expose
    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Expose
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "planned_payment_id")
    private PlannedPayment plannedPayment;

    @Override
    public String toString() {
        return  "Transaction data: " + "\n" +
                "Account: " + this.account.getId() + "\n" +
                "Description: " + this.description + "\n" +
                "Amount: " + this.amount + "\n" +
                "Currency: " + this.currency.getKind() + "\n" +
                "Date: " + this.date + "\n" +
                "Category type: " + this.category.getType().toString();
    }
}
