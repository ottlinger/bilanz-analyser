package de.aikiit.bilanzanalyser.reader;

public record BilanzRowParserStatistic(int error, int entries, int count) {
    /**
     * Derive a statistic object from the given result.
     *
     * @param result parsing result.
     * @return statistic extract.
     */
    public static BilanzRowParserStatistic from(final BilanzRowParserResult result) {
        return new BilanzRowParserStatistic(result.errorCount(), result.rows().size(), result.rowCount());
    }

    /**
     * Determine whether the statistic is empty or not.
     * @return {@code true} if the statistic does not contain any entries.
     */
    public boolean isEmpty() {
        return error == 0 && entries == 0 && count == 0;
    }
}
