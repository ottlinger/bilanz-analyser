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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static de.aikiit.bilanzanalyser.reader.BilanzRowParser.fromOdfTableRow;

@Data
@AllArgsConstructor
public class BilanzOdsReader {

    private String tableName;
    private Path source;

    /**
     * Parses the given tableName in the configured ODS file.
     *
     * @return list of {@link BilanzRow} rows.
     * @throws IOException in case of general I/O errors as parsing errors are transformed into skipped rows.
     */
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
