package com.elias.riskcomplianceservice.infrastructure.outbox.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataRiskOutboxRepository extends JpaRepository<RiskOutboxEntity, UUID> {
    List<RiskOutboxEntity> findTop100ByProcessedAtIsNullOrderByCreatedAtAsc();
}