package de.aikiit.bilanzanalyser.reader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BilanzOdsReaderTest {

    @Value("classpath:example-ausgaben.ods")
    private Resource resource;

    @Test
    void createAndReadExampleData() throws Exception {
        BilanzOdsReader reader = new BilanzOdsReader("Ausgaben", Paths.get(resource.getURI()));
        assertNotNull(reader);
        assertThat(reader.getSource()).hasFileName("example-ausgaben.ods");
        assertThat(reader.getTableName()).isEqualTo("Ausgaben");

        assertThat(reader.getRows()).isEmpty();
    }

}