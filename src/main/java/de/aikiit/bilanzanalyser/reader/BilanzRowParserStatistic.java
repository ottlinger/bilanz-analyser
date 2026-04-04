package de.aikiit.bilanzanalyser.reader;

public record BilanzRowParserStatistic(int error, int entries, int count) {
    /**
     * Derive a statistic object from the given result.
     *
     * @param result parsing result.
     * @return statistic extract.
     */
    public static BilanzRowParserStatistic from(BilanzRowParserResult result) {
        return new BilanzRowParserStatistic(result.errorCount(), result.rows().size(), result.rowCount());
    }
}
