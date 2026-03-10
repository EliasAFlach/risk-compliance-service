package com.elias.riskcomplianceservice.domain.risk;

public record RuleTrace(String ruleKey,
                        RuleOutcome outcome,
                        String message) {

}