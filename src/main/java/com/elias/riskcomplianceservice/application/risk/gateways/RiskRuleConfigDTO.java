package com.elias.riskcomplianceservice.application.risk.gateways;

public record RiskRuleConfigDTO(
        String ruleKey,
        String className,
        Integer orderIndex,
        String paramsJson
) {}
