package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzOdsReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class UploadAnalysisService {

    int rowCount(Path source, String worksheetName) throws IOException {
        BilanzOdsReader reader = new BilanzOdsReader(worksheetName, source);
        return reader.extractData().size();
    }

}
