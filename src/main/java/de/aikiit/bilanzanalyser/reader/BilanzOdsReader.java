package de.aikiit.bilanzanalyser.reader;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;

import java.io.File;

public class BilanzOdsReader {
    public static void main(String[] args) {
        try {
            OdfSpreadsheetDocument document = OdfSpreadsheetDocument.loadDocument(new File("/tmp/example.ods"));
            OdfTable table = document.getTableByName("Ausgaben"); // Change to your sheet name
            for (int row = 0; row < table.getRowCount(); row++) {
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
