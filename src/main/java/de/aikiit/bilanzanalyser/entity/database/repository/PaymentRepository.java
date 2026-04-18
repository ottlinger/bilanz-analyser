package de.aikiit.bilanzanalyser.entity.database.repository;

import de.aikiit.bilanzanalyser.entity.database.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByName(String name);
}