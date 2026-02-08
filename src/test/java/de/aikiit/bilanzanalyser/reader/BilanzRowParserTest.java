package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.math.BigDecimal;
import java.time.LocalDate;

import static de.aikiit.bilanzanalyser.reader.BilanzRowParser.cleanUpAmount;
import static de.aikiit.bilanzanalyser.reader.BilanzRowParser.fromOdfTableRow;
import static org.assertj.core.api.Assertions.assertThat;

class BilanzRowParserTest {

    @Test
    void fromOdfTableRowNPESafe() throws Exception {
        assertThat(fromOdfTableRow(null)).isEmpty();
        assertThat(fromOdfTableRow(createExampleRow())).isPresent().contains(BilanzRow.builder() //
                .date(LocalDate.parse("2025-10-02")) //
                .amount(new BigDecimal("2.34")) //
                .description("Just a test row") //
                .shop("ReWe").payment("EC").category("Lebensmittel").build());
    }

    private static OdfTableRow createExampleRow() throws Exception {
        try (OdfDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
            OdfTable t = OdfTable.newTable(document, 5, 6);
            t.setTableName("Ausgaben");
            assertThat(t.getRowCount()).isEqualTo(5);

            OdfTableRow newRow = t.getRowByIndex(0);
            assertThat(newRow.getCellCount()).isEqualTo(6);

            for (int i = 0; i < newRow.getCellCount(); i++) {
                assertThat(newRow.getCellByIndex(0)).isNotNull();
            }

            newRow.getCellByIndex(0).setStringValue("2025-10-02");
            newRow.getCellByIndex(1).setStringValue("2,34 €");
            newRow.getCellByIndex(2).setStringValue("Just a test row");
            newRow.getCellByIndex(3).setStringValue("ReWe");
            newRow.getCellByIndex(4).setStringValue("EC");
            newRow.getCellByIndex(5).setStringValue("Lebensmittel");
            return newRow;
        }
    }

    @Test
    void cleanUpAmountParsing() {
        assertThat(cleanUpAmount(null)).isNull();
        assertThat(cleanUpAmount("")).isEqualTo("");
        assertThat(cleanUpAmount(" ")).isEqualTo("");
        assertThat(cleanUpAmount("    €    ")).isEqualTo("");
        assertThat(cleanUpAmount("   1,324 €    ")).isEqualTo("1.324");
    }
}