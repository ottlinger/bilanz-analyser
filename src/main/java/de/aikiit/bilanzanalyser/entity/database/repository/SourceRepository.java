package de.aikiit.bilanzanalyser.entity.database.repository;

import de.aikiit.bilanzanalyser.entity.database.SourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<SourceEntity, Long> {

    Optional<SourceEntity> findByName(String name);
}