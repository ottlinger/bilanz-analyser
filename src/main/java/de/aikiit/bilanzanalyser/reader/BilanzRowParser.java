package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public final class BilanzRowParser {

    public static Optional<BilanzRow> fromOdfTableRow(final OdfTableRow row) {
        try {
            // in order to handle LibreOffice-specific currency values we need to parse properly and handle mixture of '.' and ',' in numbers.
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
            DecimalFormat format = new DecimalFormat();
            format.setDecimalFormatSymbols(symbols);
            format.setParseBigDecimal(true);

            final String source = cleanUpAmount(row.getCellByIndex(1).getStringValue());
            final var amount = new BigDecimal(format.parse(source).toString());
            if (BigDecimal.ZERO.equals(amount) || source.isEmpty() || "0,00".equals(source)) {
                log.debug("Amount is zero - skipping entry.");
                return Optional.empty();
            }

            var bilanzRow = BilanzRow.builder() //
                    // remove trailing spaces and currency symbol
                    .amount(amount) //
                    .description(row.getCellByIndex(2).getStringValue()) //
                    .shop(row.getCellByIndex(3).getStringValue()) //
                    .payment(row.getCellByIndex(4).getStringValue()) //
                    .category(row.getCellByIndex(5).getStringValue());

            // map special values in date column to fallback date in entity
            var sourceDate = row.getCellByIndex(0).getStringValue().trim();
            if (!sourceDate.isBlank() && !"?".equals(sourceDate)) {
                bilanzRow.date(LocalDate.parse(sourceDate));
            }

            return Optional.of(bilanzRow.build());
        } catch (Exception e) {
            log.error("Skipping row '{}' due to: {}", debugRowValues(row), e.getMessage());
            return Optional.empty();
        }
    }

    static String debugRowValues(final OdfTableRow row) {
        if (row == null) {
            return "null";
        }
        return "[" + row.getCellByIndex(0).getStringValue() + ", " + row.getCellByIndex(1).getStringValue() + ", " + row.getCellByIndex(2).getStringValue() + ", " + row.getCellByIndex(3).getStringValue() + ", " + row.getCellByIndex(4).getStringValue() + ", " + row.getCellByIndex(5).getStringValue() + "]";
    }

    /**
     * Removes currency symbol and trims the given amount value from an ODS file.
     *
     * @param amount given amount, e.g. 1,23 €
     * @return trimmed amount in order to be parseable as a numeric.
     */
    static String cleanUpAmount(final String amount) {
        if (amount != null && !amount.isEmpty()) {
            return amount.replace("€", "").trim();
        }
        return amount;
    }
}
