package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzOdsReader;
import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class UploadAnalysisService {

    /**
     * Parses and analyses a given file.
     *
     * @param spreadsheet   path to spreadsheet file.
     * @param worksheetName selected worksheet name to process.
     * @return result container.
     * @throws IOException in case of I/O problems.
     */
    BilanzRowParserResult processFile(Path spreadsheet, String worksheetName) throws IOException {
        BilanzOdsReader reader = new BilanzOdsReader(worksheetName, spreadsheet);
        return reader.extractData();
    }

}
