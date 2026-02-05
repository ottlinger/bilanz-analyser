package de.aikiit.bilanzanalyser.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BilanzOdsReader {
    public static void main(String[] args) {
        try {
            FileInputStream file = new FileInputStream(new File("example.ods"));
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    System.out.print(cellValue + "\t");
                }
                System.out.println();
            }
            workbook.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            OdfSpreadsheetDocument document = OdfSpreadsheetDocument.loadDocument(new File("example.ods"));
            OdfTable table = document.getTableByName("Sheet1"); // Change to your sheet name
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
