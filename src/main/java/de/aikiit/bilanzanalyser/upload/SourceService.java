package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.entity.database.repository.SourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class SourceService {

    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    List<String> getSources() {
        return sourceRepository.findAllNamesOrdered();
    }
}
