package com.example.financetracker.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "frequencies")
public class Frequency {

    public enum FrequencyType {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "frequency")
    @Enumerated(EnumType.STRING)
    private FrequencyType frequencyType;

}
