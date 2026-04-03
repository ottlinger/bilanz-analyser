package de.aikiit.bilanzanalyser.upload;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Log4j2
@RestController
public class UploadController {
    private static final String UPLOAD_DIR = "uploads-bilanz-analyser";
    private static List<String> RELEVANT_WORKSHEETS = List.of("Ausgaben", "Einnahmen");

    private final UploadService uploadService;

    @Value("${java.io.tmpdir}")
    private String tempDir;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @GetMapping("/upload")
    public ModelAndView upload(Model model) {
        model.addAttribute("worksheets", RELEVANT_WORKSHEETS);
        model.addAttribute("selectedWorksheet", "Ausgaben");
        return new ModelAndView("upload");
    }

    @PostMapping("/upload")
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam String selectedWorksheet, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return new ModelAndView("upload");
        }

        if (!"application/vnd.oasis.opendocument.spreadsheet".equals(file.getContentType())) {
            model.addAttribute("message", "Only ODS spreadsheet files allowed");
            return new ModelAndView("upload");
        }

        try {
            // make sure default is available
            model.addAttribute("worksheets", RELEVANT_WORKSHEETS);

            // Create directory if not exists under current temp base dir
            Path uploadDir = Paths.get(tempDir + File.separatorChar + UPLOAD_DIR);
            Files.createDirectories(uploadDir);

            // Save file
            Path destination = Paths.get(uploadDir.toString(), System.currentTimeMillis() + ".ods");
            file.transferTo(destination);

            // Process rows ....
            int rows = uploadService.rowCount(destination, selectedWorksheet);
            // and cleanup
            Files.delete(destination);

            model.addAttribute("sucmessage", "File uploaded successfully: " + file.getOriginalFilename() + " with " + rows + " rows");
        } catch (IOException e) {
            log.error(e.getMessage());
            model.addAttribute("message", "Upload failed: " + e.getMessage());
        }
        return new ModelAndView("upload");
    }

}
