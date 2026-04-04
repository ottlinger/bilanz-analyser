package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;

import java.util.List;

public record BilanzRowParserResult(
        int errorCount,
        List<BilanzRow> rows
) {

    public BilanzRowParserResult {
        rows = rows == null ? List.of() : List.copyOf(rows);
    }

    public static BilanzRowParserResult empty() {
        return new BilanzRowParserResult(0, List.of());
    }

    public BilanzRowParserResult withError() {
        return new BilanzRowParserResult(errorCount + 1, rows);
    }

    public BilanzRowParserResult withRows(List<BilanzRow> newRows) {
        return new BilanzRowParserResult(errorCount, newRows);
    }

    public BilanzRowParserResult addRow(BilanzRow row) {
        var newList = new java.util.ArrayList<>(rows);
        newList.add(row);
        return new BilanzRowParserResult(errorCount, newList);
    }
}