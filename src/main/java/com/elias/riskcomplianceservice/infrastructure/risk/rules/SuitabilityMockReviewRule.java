package com.elias.riskcomplianceservice.infrastructure.risk.rules;

import com.elias.riskcomplianceservice.domain.risk.RiskContext;
import com.elias.riskcomplianceservice.domain.risk.RiskRule;
import com.elias.riskcomplianceservice.domain.risk.RiskRuleResult;

import java.util.Map;

public class SuitabilityMockReviewRule implements RiskRule {

    @Override
    public String key() {
        return "SuitabilityMockReviewRule";
    }

    @Override
    public RiskRuleResult evaluate(RiskContext ctx, Map<String, Object> params) {
        Object configured = params.get("productId");
        if (configured != null && ctx.productId() != null && ctx.productId().toString().equals(configured.toString())) {
            return RiskRuleResult.review("Suitability requires manual review for this product");
        }
        return RiskRuleResult.pass();
    }
}