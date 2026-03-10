package com.elias.riskcomplianceservice.infrastructure.risk.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataRiskRuleConfigRepository extends JpaRepository<RiskRuleConfigEntity, UUID> {
    List<RiskRuleConfigEntity> findByEnabledTrueOrderByOrderIndexAsc();
}