package com.elias.riskcomplianceservice.domain.risk;

public record RiskRuleResult(RuleOutcome outcome, String message) {

    public static RiskRuleResult pass() {
        return new RiskRuleResult(RuleOutcome.PASS, null);
    }

    public static RiskRuleResult fail(String message) {
        return new RiskRuleResult(RuleOutcome.FAIL, message);
    }

    public static RiskRuleResult review(String message) {
        return new RiskRuleResult(RuleOutcome.REVIEW, message);
    }
}