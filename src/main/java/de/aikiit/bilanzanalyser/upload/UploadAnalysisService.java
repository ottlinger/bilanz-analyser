package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzOdsReader;
import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class UploadAnalysisService {

    BilanzRowParserResult processFile(Path spreadsheet, String worksheetName) throws IOException {
        BilanzOdsReader reader = new BilanzOdsReader(worksheetName, spreadsheet);
        return reader.extractData();
    }

}
