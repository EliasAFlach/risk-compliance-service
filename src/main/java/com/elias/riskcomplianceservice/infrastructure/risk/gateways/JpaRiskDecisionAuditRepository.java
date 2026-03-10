package com.elias.riskcomplianceservice.infrastructure.risk.gateways;

import com.elias.riskcomplianceservice.application.risk.gateways.RiskDecisionAuditRepositoryGateway;
import com.elias.riskcomplianceservice.domain.risk.RiskDecision;
import com.elias.riskcomplianceservice.infrastructure.risk.persistence.RiskDecisionAuditEntity;
import com.elias.riskcomplianceservice.infrastructure.risk.persistence.SpringDataRiskDecisionAuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaRiskDecisionAuditRepository implements RiskDecisionAuditRepositoryGateway {

    private final SpringDataRiskDecisionAuditRepository repo;
    private final ObjectMapper objectMapper;

    @Override
    public void saveAudit(UUID orderId, UUID investorId, UUID correlationId, RiskDecision decision) {
        try {
            RiskDecisionAuditEntity e = new RiskDecisionAuditEntity();
            e.setId(UUID.randomUUID());
            e.setOrderId(orderId);
            e.setInvestorId(investorId);
            e.setDecision(decision.decision().name());
            e.setReasonsJson(objectMapper.writeValueAsString(decision.reasons()));
            e.setRuleTraceJson(objectMapper.writeValueAsString(decision.ruleTrace()));
            e.setCorrelationId(correlationId);
            e.setCreatedAt(Instant.now());
            repo.save(e);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save audit", ex);
        }
    }
}