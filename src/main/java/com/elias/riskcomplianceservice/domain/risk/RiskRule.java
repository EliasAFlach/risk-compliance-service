package com.elias.riskcomplianceservice.domain.risk;

import java.util.Map;

public interface RiskRule {
    String key();
    RiskRuleResult evaluate(RiskContext ctx, Map<String, Object> params);
}