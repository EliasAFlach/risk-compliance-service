package com.elias.riskcomplianceservice.infrastructure.risk.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_decision_audit")
@Data
public class RiskDecisionAuditEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID investorId;

    @Column(nullable = false)
    private String decision;

    @Column(columnDefinition = "TEXT")
    private String reasonsJson;

    @Column(columnDefinition = "TEXT")
    private String ruleTraceJson;

    private UUID correlationId;

    @Column(nullable = false)
    private Instant createdAt;
}