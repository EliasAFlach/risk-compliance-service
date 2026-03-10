package com.elias.riskcomplianceservice.infrastructure.risk.rules;

import com.elias.riskcomplianceservice.domain.risk.RiskContext;
import com.elias.riskcomplianceservice.domain.risk.RiskRule;
import com.elias.riskcomplianceservice.domain.risk.RiskRuleResult;

import java.math.BigDecimal;
import java.util.Map;

public class MaxTotalAmountRule implements RiskRule {

    @Override
    public String key() {
        return "MaxTotalAmountRule";
    }

    @Override
    public RiskRuleResult evaluate(RiskContext ctx, Map<String, Object> params) {
        BigDecimal max = readDecimal(params.get("max"), new BigDecimal("10000.00"));

        if (ctx.totalAmount() != null && ctx.totalAmount().compareTo(max) > 0) {
            return RiskRuleResult.fail("Total amount above max allowed: " + max);
        }

        return RiskRuleResult.pass();
    }

    private BigDecimal readDecimal(Object v, BigDecimal def) {
        if (v == null) return def;
        try {
            return new BigDecimal(v.toString());
        } catch (Exception e) {
            return def;
        }
    }
}