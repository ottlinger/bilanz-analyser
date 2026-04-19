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
            entity.setDescription(row.getDescription());
            entity.setShop(getOrCreateShop(row.getShop()));
            entity.setPayment(getOrCreatePayment(row.getPayment()));
            entity.setCategory(getOrCreateCategory(row.getCategory()));
            entity.setSource(getOrCreateSource(row.getSource()));
            bilanzRowRepository.save(entity);
        }

        log.info("Successfully flushed data into database in {} ms.", (System.nanoTime() - start) / 1_000_000);
    }

    @Transactional
    ShopEntity getOrCreateShop(String name) {
        return shopRepository.findByName(name).orElseGet(() -> {
            try {
                ShopEntity entity = new ShopEntity();
                entity.setName(name);
                return shopRepository.save(entity);
            } catch (DataIntegrityViolationException e) {
                // someone else inserted it concurrently
                return shopRepository.findByName(name).orElseThrow();
            }
        });
    }

    @Transactional
    PaymentEntity getOrCreatePayment(String name) {
        return paymentRepository.findByName(name).orElseGet(() -> {
            try {
                PaymentEntity entity = new PaymentEntity();
                entity.setName(name);
                return paymentRepository.save(entity);
            } catch (DataIntegrityViolationException e) {
                // someone else inserted it concurrently
                return paymentRepository.findByName(name).orElseThrow();
            }
        });
    }

    @Transactional
    CategoryEntity getOrCreateCategory(String name) {
        return categoryRepository.findByName(name).orElseGet(() -> {
            try {
                CategoryEntity entity = new CategoryEntity();
                entity.setName(name);
                return categoryRepository.save(entity);
            } catch (DataIntegrityViolationException e) {
                // someone else inserted it concurrently
                return categoryRepository.findByName(name).orElseThrow();
            }
        });
    }

    @Transactional
    SourceEntity getOrCreateSource(String name) {
        return sourceRepository.findByName(name).orElseGet(() -> {
            try {
                SourceEntity entity = new SourceEntity();
                entity.setName(name);
                return sourceRepository.save(entity);
            } catch (DataIntegrityViolationException e) {
                // someone else inserted it concurrently
                return sourceRepository.findByName(name).orElseThrow();
            }
        });
    }

}
