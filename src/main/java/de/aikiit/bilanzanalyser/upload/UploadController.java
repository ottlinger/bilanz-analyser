package de.aikiit.bilanzanalyser.upload;

import lombok.extern.log4j.Log4j2;
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
import java.nio.file.Paths;

@Log4j2
@RestController
public class UploadController {
    private static final String UPLOAD_DIR = "uploads-bilanz-analyser/";

    @GetMapping("/upload")
    public ModelAndView upload() {
        return new ModelAndView("upload");
    }

    @PostMapping("/upload")
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file,
                                         Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return new ModelAndView("upload");
        }

        if (!"application/vnd.oasis.opendocument.spreadsheet".equals(file.getContentType())) {
            model.addAttribute("message", "Only ODS spreadsheet files allowed");
            return new ModelAndView("upload");
        }

        try {
            // Create directory if not exists
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Save file
            String filePath = UPLOAD_DIR + System.currentTimeMillis() + ".ods";
            File dest = new File(filePath);
            file.transferTo(dest);

            log.info("Processing file " + dest.getAbsolutePath());
            model.addAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            model.addAttribute("message", "Upload failed: " + e.getMessage());
        }
        return new ModelAndView("upload");
    }

}
