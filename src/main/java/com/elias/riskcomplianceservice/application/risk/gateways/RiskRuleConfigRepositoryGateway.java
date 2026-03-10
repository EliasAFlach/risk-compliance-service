package com.elias.riskcomplianceservice.application.risk.gateways;

import java.util.List;

public interface RiskRuleConfigRepositoryGateway {
    List<RiskRuleConfigDTO> findEnabledOrdered();
}