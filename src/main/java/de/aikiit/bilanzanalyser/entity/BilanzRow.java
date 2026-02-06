package de.aikiit.bilanzanalyser.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BilanzRow {
    private LocalDateTime date;
    private BigDecimal amount;
    private String description;
}
