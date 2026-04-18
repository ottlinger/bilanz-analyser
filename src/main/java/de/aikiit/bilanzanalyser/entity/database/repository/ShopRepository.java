package de.aikiit.bilanzanalyser.entity.database.repository;

import de.aikiit.bilanzanalyser.entity.database.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    Optional<ShopEntity> findByName(String name);
}