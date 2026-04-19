package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UploadController.class)
class UploadControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UploadAnalysisService uploadAnalysisService;

    @MockitoBean
    private SourceService sourceService;

    @Autowired
    private UploadController uploadController;

    @BeforeEach
    void setSourcesInController() {
        when(sourceService.getSources()).thenReturn(List.of("Ausgaben", "Einnahmen"));
        ReflectionTestUtils.setField(uploadController, "sourceService", sourceService);
    }

    @Test
    void testGetUploadPage() throws Exception {
        mockMvc.perform(get("/upload")).andExpect(status().isOk()).andExpect(view().name("upload")).andExpect(model().attributeExists("worksheets")).andExpect(model().attribute("selectedWorksheet", "Ausgaben"));
    }

    @Test
    void testHandleFileUpload_success() throws Exception {
        ReflectionTestUtils.setField(uploadController, "tempDir", System.getProperty("java.io.tmpdir"));

        MockMultipartFile file = new MockMultipartFile("file", "test.ods", "application/vnd.oasis.opendocument.spreadsheet", "dummy content".getBytes());

        when(uploadAnalysisService.processFile(eq("Ausgaben"), any(Path.class))).thenReturn(new BilanzRowParserResult(1, 2, Collections.emptyList()));

        mockMvc.perform(multipart("/upload").file(file).param("selectedWorksheet", "Ausgaben")).andExpect(status().isOk()).andExpect(view().name("upload")).andExpect(model().attributeExists("sucmessage")).andExpect(model().attributeExists("statistic")).andExpect(model().attributeExists("worksheets")).andExpect(model().attribute("selectedWorksheet", "Ausgaben"));

        verify(uploadAnalysisService, times(1)).processFile(eq("Ausgaben"), any(Path.class));
    }

    @Test
    void testHandleFileUpload_invalidWorksheet() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.ods", "application/vnd.oasis.opendocument.spreadsheet", "dummy content".getBytes());

        mockMvc.perform(multipart("/upload").file(file).param("selectedWorksheet", "INVALID")).andExpect(status().isOk()).andExpect(model().attribute("message", "Invalid worksheet selected"));

        verifyNoInteractions(uploadAnalysisService);
    }

    @Test
    void testHandleFileUpload_emptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.ods", "application/vnd.oasis.opendocument.spreadsheet", new byte[0]);

        mockMvc.perform(multipart("/upload").file(file).param("selectedWorksheet", "Ausgaben")).andExpect(status().isOk()).andExpect(model().attribute("message", "Please select a file to upload"));

        verifyNoInteractions(uploadAnalysisService);
    }

    @Test
    void testHandleFileUpload_invalidContentType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "dummy content".getBytes());

        mockMvc.perform(multipart("/upload").file(file).param("selectedWorksheet", "Ausgaben")).andExpect(status().isOk()).andExpect(model().attribute("message", "Only ODS spreadsheet files allowed"));

        verifyNoInteractions(uploadAnalysisService);
    }

    @Test
    void testHandleFileUpload_exceptionHandling() throws Exception {
        ReflectionTestUtils.setField(uploadController, "tempDir", System.getProperty("java.io.tmpdir"));

        MockMultipartFile file = new MockMultipartFile("file", "test.ods", "application/vnd.oasis.opendocument.spreadsheet", "dummy content".getBytes());

        when(uploadAnalysisService.processFile(eq("Ausgaben"), any(Path.class))).thenThrow(new IOException("Processing error"));

        mockMvc.perform(multipart("/upload").file(file).param("selectedWorksheet", "Ausgaben")).andExpect(status().isOk()).andExpect(model().attributeExists("message")).andExpect(model().attribute("message", org.hamcrest.Matchers.containsString("Upload failed")));

        verify(uploadAnalysisService, times(1)).processFile(eq("Ausgaben"), any(Path.class));
    }
}