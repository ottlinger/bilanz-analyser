package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzOdsReader;
import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class UploadAnalysisService {

    /**
     * Parses and analyses a given file.
     *
     * @param worksheetName selected worksheet name to process.
     * @return result container.
     * @param spreadsheet   path to spreadsheet file.
     * @throws IOException in case of I/O problems.
     */
    BilanzRowParserResult processFile(String worksheetName, Path spreadsheet) throws IOException {
        BilanzOdsReader reader = new BilanzOdsReader(worksheetName, spreadsheet);
        return reader.extractData();
    }

    @Async
    public void flushDataIntoDatabase(BilanzRowParserResult data) {
        // TODO #5 long-running task parsing, DB work, calling external APIs
    }

}
