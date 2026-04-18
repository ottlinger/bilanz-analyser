package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzOdsReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UploadAnalysisServiceTest {
    @Autowired
    private UploadAnalysisService uploadAnalysisService;

    @Value("classpath:example-ausgaben.ods")
    private Resource resource;

    @Test
    void processFile() throws IOException {
        uploadAnalysisService.processFile("Ausgaben", Paths.get(resource.getURI()));



    }
}