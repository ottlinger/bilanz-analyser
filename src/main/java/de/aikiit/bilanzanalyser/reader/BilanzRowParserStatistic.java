package de.aikiit.bilanzanalyser.reader;

public record BilanzRowParserStatistic(int error, int entries, int count) {
    public static BilanzRowParserStatistic from(BilanzRowParserResult result) {
        return new BilanzRowParserStatistic(result.errorCount(), result.rows().size(), result.errorCount());

    }
}
