package de.aikiit.bilanzanalyser.entity.database.repository;

import de.aikiit.bilanzanalyser.entity.database.SourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SourceRepository extends JpaRepository<SourceEntity, Long> {

    Optional<SourceEntity> findByName(String name);

    @Query("SELECT s.name FROM SourceEntity s ORDER BY s.name ASC")
    List<String> findAllNamesOrdered();
}