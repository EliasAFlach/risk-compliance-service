package com.elias.riskcomplianceservice.infrastructure.risk.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataRiskDecisionAuditRepository extends JpaRepository<RiskDecisionAuditEntity, UUID> {}