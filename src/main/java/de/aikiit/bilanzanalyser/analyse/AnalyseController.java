package de.aikiit.bilanzanalyser.analyse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyseController {
    @GetMapping("/analyse")
    public String analyse() {
        return "Ready to analyse!";
    }
}
