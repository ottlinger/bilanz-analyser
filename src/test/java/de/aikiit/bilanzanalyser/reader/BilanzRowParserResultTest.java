package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class BilanzRowParserResultTest {

    @Test
    void constructor_shouldHandleNullRowsAsEmptyList() {
        BilanzRowParserResult result = new BilanzRowParserResult(1, 2, null);

        assertNotNull(result.rows());
        assertTrue(result.rows().isEmpty());
    }

    @Test
    void empty_shouldReturnZeroCountsAndEmptyList() {
        BilanzRowParserResult result = BilanzRowParserResult.empty();

        assertEquals(0, result.errorCount());
        assertEquals(0, result.rowCount());
        assertNotNull(result.rows());
        assertTrue(result.rows().isEmpty());
    }

    @Test
    void withError_shouldIncrementErrorCountOnly() {
        BilanzRowParserResult original = new BilanzRowParserResult(1, 2, List.of());

        BilanzRowParserResult updated = original.withError();

        assertEquals(2, updated.errorCount());
        assertEquals(2, updated.rowCount());
        assertEquals(original.rows(), updated.rows());
    }

    @Test
    void withRow_shouldIncrementRowCountOnly() {
        BilanzRowParserResult original = new BilanzRowParserResult(1, 2, List.of());

        BilanzRowParserResult updated = original.withRow();

        assertEquals(1, updated.errorCount());
        assertEquals(3, updated.rowCount());
        assertEquals(original.rows(), updated.rows());
    }

    @Test
    void withRows_shouldReplaceRows() {
        BilanzRow row1 = mock(BilanzRow.class);
        BilanzRow row2 = mock(BilanzRow.class);

        BilanzRowParserResult original = new BilanzRowParserResult(0, 0, List.of());

        BilanzRowParserResult updated = original.withRows(List.of(row1, row2));

        assertEquals(2, updated.rows().size());
        assertTrue(updated.rows().containsAll(List.of(row1, row2)));
    }

    @Test
    void addRow_shouldAppendRow() {
        BilanzRow row1 = mock(BilanzRow.class);
        BilanzRow row2 = mock(BilanzRow.class);

        BilanzRowParserResult original = new BilanzRowParserResult(0, 0, List.of(row1));

        BilanzRowParserResult updated = original.addRow(row2);

        assertEquals(2, updated.rows().size());
        assertEquals(row1, updated.rows().get(0));
        assertEquals(row2, updated.rows().get(1));
    }

    @Test
    void rows_shouldBeImmutable() {
        BilanzRow row = mock(BilanzRow.class);

        BilanzRowParserResult result = new BilanzRowParserResult(0, 0, List.of(row));

        assertThrows(UnsupportedOperationException.class, () -> {
            result.rows().add(mock(BilanzRow.class));
        });
    }

    @Test
    void addRow_shouldNotMutateOriginalInstance() {
        BilanzRow row1 = mock(BilanzRow.class);
        BilanzRow row2 = mock(BilanzRow.class);

        BilanzRowParserResult original = new BilanzRowParserResult(0, 0, List.of(row1));

        BilanzRowParserResult updated = original.addRow(row2);

        assertEquals(1, original.rows().size());
        assertEquals(2, updated.rows().size());
    }
}