package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import de.aikiit.bilanzanalyser.reader.BilanzRowParserStatistic;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
@RestController
public final class UploadController {
    private static final String UPLOAD_DIR = "uploads-bilanz-analyser";

    private final UploadAnalysisService uploadAnalysisService;
    private final SourceService sourceService;

    @Value("${java.io.tmpdir}")
    private String tempDir;

    public UploadController(UploadAnalysisService uploadAnalysisService, SourceService sourceService) {
        this.uploadAnalysisService = uploadAnalysisService;
        this.sourceService = sourceService;
    }

    @GetMapping("/upload")
    public ModelAndView upload() {
        ModelAndView mav = new ModelAndView("upload");
        mav.addObject("worksheets", sourceService.getSources());
        mav.addObject("selectedWorksheet", "Ausgaben");
        return mav;
    }

    @PostMapping("/upload")
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("selectedWorksheet") String selectedWorksheet) {

        // Create ModelAndView for the "upload" view
        ModelAndView mav = new ModelAndView("upload");
        mav.addObject("worksheets", sourceService.getSources());

        // prevent mingling with selected worksheet and properly escape user-provided value
        if (!sourceService.getSources().contains(selectedWorksheet)) {
            mav.addObject("message", "Invalid worksheet selected");
            return mav;
        }
        String escapedSelectedWorksheet = HtmlUtils.htmlEscape(selectedWorksheet);
        mav.addObject("selectedWorksheet", escapedSelectedWorksheet);

        if (file.isEmpty()) {
            mav.addObject("message", "Please select a file to upload");
            return mav;
        }

        if (!"application/vnd.oasis.opendocument.spreadsheet".equals(file.getContentType())) {
            mav.addObject("message", "Only ODS spreadsheet files allowed");
            return mav;
        }

        try {
            // Create directory if not exists under current temp base dir
            Path uploadDir = Paths.get(tempDir + File.separatorChar + UPLOAD_DIR);
            Files.createDirectories(uploadDir);

            // Save file
            Path destination = Paths.get(uploadDir.toString(), System.currentTimeMillis() + ".ods");
            file.transferTo(destination);

            // Process rows and cleanup afterwards
            BilanzRowParserResult result = uploadAnalysisService.processFile(selectedWorksheet, destination);
            Files.delete(destination);
            // flush to DB asynchronously
            uploadAnalysisService.flushDataIntoDatabase(result);

            mav.addObject("sucmessage", "File uploaded successfully. Processed " + result.rowCount() + " rows in table " + escapedSelectedWorksheet);
            mav.addObject("statistic", BilanzRowParserStatistic.from(result));
        } catch (IOException e) {
            log.error(e.getMessage());
            mav.addObject("message", "Upload failed: " + e.getMessage());
        }
        return mav;
    }

}
