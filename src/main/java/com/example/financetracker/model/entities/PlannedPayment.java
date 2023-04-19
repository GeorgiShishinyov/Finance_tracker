package com.example.financetracker.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "planned_payments")
public class PlannedPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "frequency_id")
    private Frequency frequency;

    @Override
    public String toString() {
        return  "Planned payment data: " + "\n" +
                "Account: " + this.account.getId() + "\n" +
                "Description: " + this.description + "\n" +
                "Amount: " + this.amount + "\n" +
                "Date: " + this.date + "\n" +
                "Category type: " + this.category.getType().toString() + "\n" +
                "Frequency: " + frequency.getFrequency().toString();
    }
}
