package de.aikiit.bilanzanalyser.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BilanzRow {
    private LocalDate date;
    private BigDecimal amount;
    private String description;
}
