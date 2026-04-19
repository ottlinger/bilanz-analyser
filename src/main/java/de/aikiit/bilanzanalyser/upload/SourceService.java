package de.aikiit.bilanzanalyser.upload;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class SourceService {

    List<String> getSources() {
        return List.of("Ausgaben", "Einnahmen");
    }
}
