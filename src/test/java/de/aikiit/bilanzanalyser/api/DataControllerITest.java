package de.aikiit.bilanzanalyser.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DataControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnDataItems() throws Exception {
        String response = mockMvc.perform(get("/api/dataitems")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        List<DataItem> result = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertThat(result).hasSize(3);

        // structure checks
        assertThat(result.get(0).name()).isNotBlank();
        assertThat(result.get(0).value()).isGreaterThanOrEqualTo(0);

        // deterministic check, rest is random
        assertThat(result).anyMatch(item -> item.name().equals("Banana") && item.value() == 234);
        assertThat(result).anyMatch(item -> item.name().equals("Apple"));
        assertThat(result).anyMatch(item -> item.name().equals("Tea"));
    }
}