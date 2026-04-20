package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public final class BilanzRowParser {

    public static Optional<BilanzRow> fromOdfTableRow(final OdfTableRow row) {
        try {
            var bilanzRow = BilanzRow.builder() //
                    // remove trailing spaces and currency symbol
                    .amount(new BigDecimal(cleanUpAmount(row.getCellByIndex(1).getStringValue()))) //
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
            log.error("Skipping row due to: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Removes currency symbol, changes <code>,</code> to <code>.</code> and trims the given amount value from an ODS file.
     *
     * @param amount given amount, e.g. 1,23 €
     * @return trimmed amount in order to be parseable as a numeric.
     */
    static String cleanUpAmount(String amount) {
        if (amount != null && !amount.isEmpty()) {
            return amount.replace("€", "").replace(",", ".").trim();
        }
        return amount;
    }
}
