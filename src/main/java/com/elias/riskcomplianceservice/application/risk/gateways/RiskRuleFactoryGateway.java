package com.elias.riskcomplianceservice.application.risk.gateways;

import com.elias.riskcomplianceservice.domain.risk.RiskRule;

public interface RiskRuleFactoryGateway {
    RiskRule create(String className);
}