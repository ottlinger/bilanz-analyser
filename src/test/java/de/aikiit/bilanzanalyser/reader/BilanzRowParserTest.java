package de.aikiit.bilanzanalyser.reader;

import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.util.List;
import java.util.Optional;

import static de.aikiit.bilanzanalyser.reader.BilanzRowParser.cleanUpAmount;
import static de.aikiit.bilanzanalyser.reader.BilanzRowParser.fromOdfTableRow;
import static org.assertj.core.api.Assertions.assertThat;

class BilanzRowParserTest {

    @Test
    void fromOdfTableRowNPESafe() throws Exception {
        assertThat(fromOdfTableRow(null)).isEmpty();
        assertThat(fromOdfTableRow(createExampleRow())).isEmpty();
    }

    private static OdfTableRow createExampleRow() throws Exception {
        try (OdfDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
            final Optional<OdfTable> first = document.getTableList(true).stream().findFirst();
            if (first.isPresent()) {
                OdfTable t = first.get();
                List<OdfTableRow> newRows = t.appendRows(5);
                newRows.get(0).getCellByIndex(0).setStringValue("2025-10-02");
                newRows.get(0).getCellByIndex(1).setStringValue("2,34 €");
                newRows.get(0).getCellByIndex(2).setStringValue("Just a test row");
                newRows.get(0).getCellByIndex(3).setStringValue("ReWe");
                newRows.get(0).getCellByIndex(4).setStringValue("EC");
                newRows.get(0).getCellByIndex(4).setStringValue("Lebensmittel");
            }
        }
        throw new IllegalArgumentException("No table found");
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