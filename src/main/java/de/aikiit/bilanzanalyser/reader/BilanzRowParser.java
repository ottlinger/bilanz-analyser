package de.aikiit.bilanzanalyser.reader;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BilanzRowParser {

    public static Optional<BilanzRow> fromOdfTableRow(OdfTableRow row) {
        try {
            BilanzRow bilanzRow = new BilanzRow();
            // expected format: yyyy-MM-dd
            bilanzRow.setDate(LocalDate.parse(row.getCellByIndex(0).getStringValue()));
            // remove trailing spaces and currency symbol
            bilanzRow.setAmount(new BigDecimal(cleanUpAmount(row.getCellByIndex(1).getStringValue())));
            bilanzRow.setDescription(row.getCellByIndex(2).getStringValue());
            bilanzRow.setShop(row.getCellByIndex(3).getStringValue());
            bilanzRow.setPayment(row.getCellByIndex(4).getStringValue());
            bilanzRow.setCategory(row.getCellByIndex(5).getStringValue());

            return Optional.of(bilanzRow);
        } catch (Exception e) {
            System.err.println("Skipping row due to: " + e.getMessage());
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
            return amount.replaceAll("€", "").replaceAll(",", ".").trim();
        }
        return amount;
    }
}
