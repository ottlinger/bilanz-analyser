package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;

import java.util.List;

/**
 * Result of parsing a spreadsheet file with
 *
 * @param errorCount error row count.
 * @param rowCount   row count equals the number seen, not parsed correctly.
 * @param rows       extracted and parsed rows. May be less than rowCount.
 */
public record BilanzRowParserResult(
        int errorCount,
        int rowCount,
        List<BilanzRow> rows
) {

    public BilanzRowParserResult {
        rows = rows == null ? List.of() : List.copyOf(rows);
    }

    public static BilanzRowParserResult empty() {
        return new BilanzRowParserResult(0, 0, List.of());
    }

    /**
     * Increment errorCount.
     *
     * @return copy with incremented value.
     */
    public BilanzRowParserResult withError() {
        return new BilanzRowParserResult(errorCount + 1, rowCount, rows);
    }

    /**
     * Increment rowCount.
     *
     * @return copy with incremented value.
     */
    public BilanzRowParserResult withRow() {
        return new BilanzRowParserResult(errorCount, rowCount + 1, rows);
    }

    /**
     * Allows adding a list of rows.
     *
     * @param newRows a list of rows to replace any existing rows.
     * @return copy with added row.
     */
    public BilanzRowParserResult withRows(List<BilanzRow> newRows) {
        return new BilanzRowParserResult(errorCount, rowCount, newRows);
    }

    /**
     * Allows adding a single row incrementally.
     *
     * @param row a single row.
     * @return copy with added row.
     */
    public BilanzRowParserResult addRow(BilanzRow row) {
        var newList = new java.util.ArrayList<>(rows);
        newList.add(row);
        return new BilanzRowParserResult(errorCount, rowCount, newList);
    }
}