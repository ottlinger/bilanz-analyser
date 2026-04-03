package de.aikiit.bilanzanalyser.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class UploadController {
    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/handle-upload")
    public ResponseEntity<String> uploadFileRequestParam(File file) {
        // TODO Handle the file upload logic here
        // Save the file or it as needed
        return ResponseEntity.ok("File uploaded successfully: " + file.getName());
    }
}
