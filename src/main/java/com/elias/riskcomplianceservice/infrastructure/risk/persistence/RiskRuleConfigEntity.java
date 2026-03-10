package com.elias.riskcomplianceservice.infrastructure.risk.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_rule_config")
@Data
public class RiskRuleConfigEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String ruleKey;

    @Column(nullable = false, length = 500)
    private String className;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(columnDefinition = "TEXT")
    private String paramsJson;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;
}