package de.aikiit.bilanzanalyser.upload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UploadAnalysisServiceTest {
    @Autowired
    private UploadAnalysisService uploadAnalysisService;

    @Value("classpath:example-ausgaben.ods")
    private Resource resource;

    @Test
    void processFile() throws IOException {
        var result = uploadAnalysisService.processFile("Ausgaben", Paths.get(resource.getURI()));
        assertThat(result).isNotNull();

        assertThat(result.rows()).hasSize(152);
        assertThat(result.errorCount()).isEqualTo(3);
        assertThat(result.rowCount()).isEqualTo(155);
    }

    @Test
    void processFileWithUnknownSpreadsheet() throws IOException {
        var result = uploadAnalysisService.processFile("DoesNotExist", Paths.get(resource.getURI()));
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
    }
}