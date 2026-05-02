package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public final class BilanzOdsReader {

    private String tableName;
    private Path source;

    /**
     * Parses the given tableName in the configured ODS file.
     *
     * @return result object.
     * @throws IOException in case of general I/O errors as parsing errors are transformed into skipped rows.
     */
    public BilanzRowParserResult extractData() throws IOException {
        try {
            BilanzRowParserResult result = BilanzRowParserResult.empty();

            OdfTable table = readTable();
            if (table == null) {
                return result;
            }

            // ODS default is 1048576 albeit it's only empty rows
            log.info("Given table '{}' has {} rows", this.tableName, table.getRowCount());

            List<BilanzRow> rows = new ArrayList<>();
            AtomicInteger counter = new AtomicInteger(0);
            AtomicInteger readRows = new AtomicInteger(0);
            AtomicInteger rowsWithParsingErrors = new AtomicInteger(0);

            for (int rowCount = 0; rowCount < table.getRowCount(); rowCount++) {
                OdfTableRow row = table.getRowByIndex(rowCount);

                // only allow 5 empty rows in a row
                if (counter.get() == 5) {
                    log.info("STOPPING due to too many empty lines after having read {} non-empty rows.", readRows.get());
                    break;
                }

                OdfTableCell cell = row.getCellByIndex(0);
                if (cell.getStringValue().isEmpty()) {
                    counter.incrementAndGet();
                    // skip empty rows
                } else {
                    counter.set(0);
                    readRows.incrementAndGet();
                    result = result.withRow();

                    Optional<BilanzRow> br = fromOdfTableRow(row);
                    if (br.isPresent()) {
                        rows.add(br.get());
                    } else {
                        rowsWithParsingErrors.incrementAndGet();
                        result = result.withError();
                    }
                }
            }

            log.info("Extracted {} rows successfully, while skipping {} not well formatted rows.", rows.size(), rowsWithParsingErrors.get());
            return result.withRows(rows);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private OdfTable readTable() throws Exception {
        try (OdfSpreadsheetDocument document = OdfSpreadsheetDocument.loadDocument(source.toFile())) {
            return document.getTableByName(this.tableName);
        }
    }
}
