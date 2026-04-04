package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BilanzRowParserStatisticTest {

    @Test
    void testFrom_ShouldCreateCorrectStatistic() {
        BilanzRowParserResult result = mock(BilanzRowParserResult.class);
        when(result.errorCount()).thenReturn(42);
        when(result.rowCount()).thenReturn(32);
        when(result.rows()).thenReturn(List.of(BilanzRow.builder().build(), BilanzRow.builder().build(), BilanzRow.builder().build()));

        BilanzRowParserStatistic statistic = BilanzRowParserStatistic.from(result);

        assertEquals(42, statistic.error());
        assertEquals(3, statistic.entries());
        assertEquals(32, statistic.count());

        verify(result).errorCount();
        verify(result).rows();
    }
}