package de.aikiit.bilanzanalyser.entity.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class BilanzRowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private BigDecimal amount;
    private String description;

    @ManyToOne
    private ShopEntity shop;
    @ManyToOne
    private PaymentEntity payment;
    @ManyToOne
    private CategoryEntity category;
    @ManyToOne
    private SourceEntity source; // e.g. Ausgaben/Einnahmen

}
