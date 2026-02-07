package de.aikiit.bilanzanalyser.reader;

import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

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
                return first.get().appendRow();
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