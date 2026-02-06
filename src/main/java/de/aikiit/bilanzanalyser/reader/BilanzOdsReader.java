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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
public class BilanzOdsReader {

    private String tableName;
    private Path source;

    public List<BilanzRow> getRows() throws IOException {
        try {
            OdfTable table = readTable();
            System.out.println("Zeilen: " + table.getRowCount());

            List<BilanzRow> rows = new ArrayList<>();
            AtomicInteger counter = new AtomicInteger(0);

            emptyRow:
            for (int row = 0; row < table.getRowCount(); row++) {
                // only allow 3 empty rows in a row
                if (counter.get() == 3) {
                    System.out.println("ABBRUCH");
                    break;
                }
                for (int col = 0; col < table.getColumnCount(); col++) {
                    OdfTableCell cell = table.getCellByPosition(col, row);
                    if (cell.getStringValue().isEmpty()) {
                        counter.incrementAndGet();
                        break emptyRow;
                    } else {
                        System.out.println("RESET");
                        counter.set(0);
                    }
                    System.out.print(cell.getStringValue() + "\t");
                }
                System.out.println();
            }

            return rows;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private static Optional<BilanzRow> fromOdfTableRow(OdfTableRow row) {
        try {
            BilanzRow bilanzRow = new BilanzRow();
            bilanzRow.setDate(LocalDateTime.parse(row.getCellByIndex(0).getStringValue()));
            bilanzRow.setAmount(new BigDecimal(row.getCellByIndex(1).getStringValue()));
            bilanzRow.setDescription(row.getCellByIndex(2).getStringValue());
            return Optional.of(bilanzRow);
        } catch (Exception e) {
            return Optional.empty();
        }
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
