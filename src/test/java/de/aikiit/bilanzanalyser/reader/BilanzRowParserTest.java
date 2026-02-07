package de.aikiit.bilanzanalyser.reader;

import org.junit.jupiter.api.Test;

import static de.aikiit.bilanzanalyser.reader.BilanzRowParser.cleanUpAmount;
import static org.assertj.core.api.Assertions.assertThat;

class BilanzRowParserTest {

    @Test
    void fromOdfTableRow() {
        // tbd
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