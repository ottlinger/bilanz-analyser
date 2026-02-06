package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
public class BilanzOdsReader {

    private String tableName;
    private Path source;

    public List<BilanzRow> extractData() throws IOException {
        try {
            OdfTable table = readTable();
            // ODS default is 1048576 albeit it's only empty rows
            System.out.println("Given table '" + this.tableName + "' has " + table.getRowCount() + " rows");

            List<BilanzRow> rows = new ArrayList<>();
            AtomicInteger counter = new AtomicInteger(0);
            AtomicInteger readRows = new AtomicInteger(0);
            AtomicInteger rowsWithParsingErrors = new AtomicInteger(0);

            for (int rowCount = 0; rowCount < table.getRowCount(); rowCount++) {
                OdfTableRow row = table.getRowByIndex(rowCount);

                // only allow 5 empty rows in a row
                if (counter.get() == 5) {
                    System.out.println("STOPPING due to too many empty lines after having read " + readRows.get() + " non-empty rows.");
                    break;
                }

                OdfTableCell cell = row.getCellByIndex(0);
                if (cell.getStringValue().isEmpty()) {
                    counter.incrementAndGet();
                    // skip empty rows
                } else {
                    counter.set(0);
                    readRows.incrementAndGet();

                    Optional<BilanzRow> br = fromOdfTableRow(row);
                    if (br.isPresent()) {
                        rows.add(br.get());
                    } else {
                        rowsWithParsingErrors.incrementAndGet();
                    }
                }
            }

            System.out.println("Extracted " + rows.size() + " rows successfully, while skipping " + rowsWithParsingErrors.get() + " not well formatted rows.");
            return rows;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private static Optional<BilanzRow> fromOdfTableRow(OdfTableRow row) {
        try {
            BilanzRow bilanzRow = new BilanzRow();
            // expected format: yyyy-MM-dd
            bilanzRow.setDate(LocalDate.parse(row.getCellByIndex(0).getStringValue()));
            // remove trailing spaces and currency symbol
            bilanzRow.setAmount(new BigDecimal(cleanUpAmount(row.getCellByIndex(1).getStringValue())));
            bilanzRow.setDescription(row.getCellByIndex(2).getStringValue());

            return Optional.of(bilanzRow);
        } catch (Exception e) {
            System.err.println("Skipping row due to: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Removes currency symbol, changes <code>,</code> to <code>.</code> and trims the given amount value from an ODS file.
     *
     * @param amount given amount, e.g. 1,23 €
     * @return trimmed amount in order to be parseable as a numeric.
     */
    private static String cleanUpAmount(String amount) {
        if (amount != null && !amount.isEmpty()) {
            return amount.replaceAll("€", "").replaceAll(",", ".").trim();
        }
        return amount;
    }

    private OdfTable readTable() throws Exception {
        try (OdfSpreadsheetDocument document = OdfSpreadsheetDocument.loadDocument(source.toFile())) {
            return document.getTableByName(this.tableName);
        }
    }

    public static void main(String[] args) {
        try {
            OdfSpreadsheetDocument document = OdfSpreadsheetDocument.loadDocument(new File("/tmp/example.ods"));
            OdfTable table = document.getTableByName("Ausgaben");
            System.out.println(table.getRowCount() + " lines to read");

            for (int row = 0; row < 10 /*table.getRowCount() */; row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    OdfTableCell cell = table.getCellByPosition(col, row);
                    System.out.print(cell.getStringValue() + "\t");
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
