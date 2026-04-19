package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzOdsReader;
import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
@Log4j2
public class UploadAnalysisService {

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

        // TODO #5 long-running task parsing, DB work, calling external APIs
        log.info("Successfully flushed data into database in {} ms.", (System.nanoTime() - start) / 1_000_000);
    }

}
