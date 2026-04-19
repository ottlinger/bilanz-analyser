package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import de.aikiit.bilanzanalyser.entity.database.BilanzRowEntity;
import de.aikiit.bilanzanalyser.entity.database.CategoryEntity;
import de.aikiit.bilanzanalyser.entity.database.PaymentEntity;
import de.aikiit.bilanzanalyser.entity.database.ShopEntity;
import de.aikiit.bilanzanalyser.entity.database.SourceEntity;
import de.aikiit.bilanzanalyser.entity.database.repository.BilanzRowRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.CategoryRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.PaymentRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.ShopRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.SourceRepository;
import de.aikiit.bilanzanalyser.reader.BilanzOdsReader;
import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
public class UploadAnalysisService {
    private final BilanzRowRepository bilanzRowRepository;
    private final ShopRepository shopRepository;
    private final PaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;
    private final SourceRepository sourceRepository;

    public UploadAnalysisService(BilanzRowRepository bilanzRowRepository, ShopRepository shopRepository, PaymentRepository paymentRepository, CategoryRepository categoryRepository, SourceRepository sourceRepository) {
        this.bilanzRowRepository = bilanzRowRepository;
        this.shopRepository = shopRepository;
        this.paymentRepository = paymentRepository;
        this.categoryRepository = categoryRepository;
        this.sourceRepository = sourceRepository;
    }

    /**
     * Parses and analyses a given file.
     *
     * @param worksheetName selected worksheet name to process.
     * @param spreadsheet   path to spreadsheet file.
     * @return result container.
     * @throws IOException in case of I/O problems.
     */
    BilanzRowParserResult processFile(String worksheetName, Path spreadsheet) throws IOException {
        BilanzOdsReader reader = new BilanzOdsReader(worksheetName, spreadsheet);
        return reader.extractData();
    }

    @Async
    public void flushDataIntoDatabase(BilanzRowParserResult data) {
        long start = System.nanoTime();
        log.info("Starting to flush data into database...");

        for (BilanzRow row : data.rows()) {
            BilanzRowEntity entity = new BilanzRowEntity();
            entity.setDate(row.getDate());
            entity.setAmount(row.getAmount());
            entity.setDescription(replaceIfEmpty(row.getDescription()));
            entity.setShop(getOrCreateShop(row.getShop()));
            entity.setPayment(getOrCreatePayment(row.getPayment()));
            entity.setCategory(getOrCreateCategory(row.getCategory()));
            entity.setSource(getOrCreateSource(row.getSource()));
            bilanzRowRepository.save(entity);
        }

        log.info("Successfully flushed data into database in {} ms.", (System.nanoTime() - start) / 1_000_000);
    }

    /**
     * Retrieves an entity by name or creates and persists it if it does not exist,
     * assuming that the underlying entity enforces uniqueness with a database constraint on the column 'name'.
     * <p>
     * This method implements a "get-or-create" pattern with basic concurrency handling:
     * it first attempts to find an existing entity using the provided {@code finder}.
     * If none is found, it invokes the {@code creator} to create and persist a new entity.
     * <p>
     * In case of a concurrent insert (e.g. due to a unique constraint on the name),
     * a {@link DataIntegrityViolationException} may be thrown during creation. In that case,
     * the method retries the lookup and returns the entity inserted by the concurrent transaction.
     * <p>
     * The input name is normalized using {@link #replaceIfEmpty(String)} before lookup and creation.
     *
     * @param rawName the original name value; may be {@code null} or blank
     * @param finder  function used to look up an existing entity by normalized name
     * @param creator function that creates and persists a new entity for the normalized name;
     *                must return a fully initialized and saved entity
     * @param <T>     the entity type
     * @return an existing or newly created entity corresponding to the given name.
     */
    @Transactional
    <T> T getOrCreate(String rawName, Function<String, Optional<T>> finder, Function<String, T> creator) {
        String name = replaceIfEmpty(rawName);

        return finder.apply(name).orElseGet(() -> {
            try {
                return creator.apply(name);
            } catch (DataIntegrityViolationException e) {
                // someone else inserted it concurrently
                return finder.apply(name).orElseThrow();
            }
        });
    }

    @Transactional
    ShopEntity getOrCreateShop(String name) {
        return getOrCreate(name, shopRepository::findByName, n -> {
            ShopEntity e = new ShopEntity();
            e.setName(n);
            return shopRepository.save(e);
        });
    }

    @Transactional
    PaymentEntity getOrCreatePayment(String name) {
        return getOrCreate(name, paymentRepository::findByName, n -> {
            PaymentEntity e = new PaymentEntity();
            e.setName(n);
            return paymentRepository.save(e);
        });
    }

    @Transactional
    CategoryEntity getOrCreateCategory(String name) {
        return getOrCreate(name, categoryRepository::findByName, n -> {
            CategoryEntity e = new CategoryEntity();
            e.setName(n);
            return categoryRepository.save(e);
        });
    }

    @Transactional
    SourceEntity getOrCreateSource(String name) {
        return getOrCreate(name, sourceRepository::findByName, n -> {
            SourceEntity e = new SourceEntity();
            e.setName(n); // no replaceIfEmpty here before, but now consistent
            return sourceRepository.save(e);
        });
    }

    String replaceIfEmpty(String value) {
        return value == null || value.isBlank() ? "<empty>" : value.trim();
    }

}
