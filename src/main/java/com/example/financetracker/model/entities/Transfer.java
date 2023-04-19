package com.example.financetracker.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_sender_id")
    private Account accountSender;

    @ManyToOne
    @JoinColumn(name = "account_receiver_id")
    private Account accountReceiver;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "date")
    private LocalDateTime date;

    @Override
    public String toString() {
        return  "Transfer data: " + "\n" +
                "Account sender Id: " + this.accountSender.getId() + "\n" +
                "Account receiver Id: " + this.accountReceiver.getId() + "\n" +
                "Description: " + this.description + "\n" +
                "Amount: " + this.amount + "\n" +
                "Date: " + this.date;
    }
}
