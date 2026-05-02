package de.aikiit.bilanzanalyser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public final class WebAppApplication {
    public static void main(final String[] args) {
        SpringApplication.run(WebAppApplication.class, args);
    }
}
