package com.elias.riskcomplianceservice.application.risk.gateways;

import com.elias.riskcomplianceservice.domain.risk.RiskDecision;

import java.util.UUID;

public interface RiskDecisionAuditRepositoryGateway {
    void saveAudit(UUID orderId, UUID investorId, UUID correlationId, RiskDecision decision);
}