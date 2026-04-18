package de.aikiit.bilanzanalyser.entity.database.repository;

import de.aikiit.bilanzanalyser.entity.database.BilanzRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BilanzRowRepository extends JpaRepository<BilanzRowEntity, Long> {
}