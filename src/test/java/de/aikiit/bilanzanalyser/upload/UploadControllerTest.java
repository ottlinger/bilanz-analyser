package de.aikiit.bilanzanalyser.upload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UploadController uploadController;

    @BeforeEach
    void setUp() {
        // Set tempDir manually (since @Value is not injected in unit tests)
        ReflectionTestUtils.setField(uploadController, "tempDir", System.getProperty("java.io.tmpdir"));
    }

    @Test
    void testUploadPage() {
        ModelAndView mav = uploadController.upload();

        assertEquals("upload", mav.getViewName());
        assertTrue(mav.getModel().containsKey("worksheets"));
        assertEquals("Ausgaben", mav.getModel().get("selectedWorksheet"));
    }

    @Test
    void testHandleFileUpload_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.ods",
                "application/vnd.oasis.opendocument.spreadsheet",
                "dummy content".getBytes()
        );

        when(uploadService.rowCount(any(Path.class), eq("Ausgaben"))).thenReturn(42);

        ModelAndView mav = uploadController.handleFileUpload(file, "Ausgaben");

        assertEquals("upload", mav.getViewName());
        assertTrue(mav.getModel().containsKey("sucmessage"));
        assertTrue(((String) mav.getModel().get("sucmessage")).contains("42"));

        verify(uploadService, times(1)).rowCount(any(Path.class), eq("Ausgaben"));
    }

    @Test
    void testHandleFileUpload_invalidWorksheet() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.ods",
                "application/vnd.oasis.opendocument.spreadsheet",
                "dummy content".getBytes()
        );

        ModelAndView mav = uploadController.handleFileUpload(file, "INVALID");

        assertEquals("upload", mav.getViewName());
        assertEquals("Invalid worksheet selected", mav.getModel().get("message"));

        verifyNoInteractions(uploadService);
    }

    @Test
    void testHandleFileUpload_emptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.ods",
                "application/vnd.oasis.opendocument.spreadsheet",
                new byte[0]
        );

        ModelAndView mav = uploadController.handleFileUpload(file, "Ausgaben");

        assertEquals("Please select a file to upload", mav.getModel().get("message"));
        verifyNoInteractions(uploadService);
    }

    @Test
    void testHandleFileUpload_invalidContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "dummy content".getBytes()
        );

        ModelAndView mav = uploadController.handleFileUpload(file, "Ausgaben");

        assertEquals("Only ODS spreadsheet files allowed", mav.getModel().get("message"));
        verifyNoInteractions(uploadService);
    }

    @Test
    void testHandleFileUpload_exceptionHandling() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.ods",
                "application/vnd.oasis.opendocument.spreadsheet",
                "dummy content".getBytes()
        );

        when(uploadService.rowCount(any(Path.class), eq("Ausgaben")))
                .thenThrow(new IOException("Processing error"));

        ModelAndView mav = uploadController.handleFileUpload(file, "Ausgaben");

        assertTrue(((String) mav.getModel().get("message")).contains("Upload failed"));
        verify(uploadService, times(1)).rowCount(any(Path.class), eq("Ausgaben"));
    }
}