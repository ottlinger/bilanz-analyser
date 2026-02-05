package de.aikiit.bilanzanalyser.upload;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class UploadController {
    @GetMapping("/upload")
    public String upload() {
        return "Hello, World!";
    }
}
