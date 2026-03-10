package com.elias.riskcomplianceservice.domain.risk;

import com.elias.investcommon.domain.RiskDecisionType;

import java.util.List;

public record RiskDecision(
        RiskDecisionType decision,
        List<String> reasons,
        List<RuleTrace> ruleTrace
) {}