package de.aikiit.bilanzanalyser.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BilanzRow {
    /**
     * Default fallback indicates that no date was set, e.g. empty date or '?'.
     */
    public static final LocalDate FALLBACK_DATE = LocalDate.of(1970, 1, 1);

    @Builder.Default
    private LocalDate date = FALLBACK_DATE;
    private BigDecimal amount;
    private String description;
    private String shop;
    private String payment;
    private String category;
    @Builder.Default
    private String source = "Ausgaben";
}
