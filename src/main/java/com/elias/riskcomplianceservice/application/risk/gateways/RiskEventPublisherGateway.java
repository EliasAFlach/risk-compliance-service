package com.elias.riskcomplianceservice.application.risk.gateways;

import com.elias.investcommon.event.risk.RiskCheckRequestedEvent;
import com.elias.riskcomplianceservice.domain.risk.RiskDecision;

import java.util.UUID;

public interface RiskEventPublisherGateway {
    void publishDecision(RiskCheckRequestedEvent input, RiskDecision decision, UUID correlationId);
}