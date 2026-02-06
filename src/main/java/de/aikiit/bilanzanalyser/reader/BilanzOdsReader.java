package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class BilanzOdsReader {

    private String tableName;
    private Path source;

    public List<BilanzRow> getRows() throws IOException {
        return Collections.EMPTY_LIST;
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
