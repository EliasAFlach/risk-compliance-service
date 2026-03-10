package com.elias.riskcomplianceservice.infrastructure.risk.gateways;

import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleFactoryGateway;
import com.elias.riskcomplianceservice.domain.risk.RiskRule;
import org.springframework.stereotype.Component;

@Component
public class ReflectionRiskRuleFactory implements RiskRuleFactoryGateway {

    @Override
    public RiskRule create(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            if (!RiskRule.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Class does not implement RiskRule: " + className);
            }

            return (RiskRule) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate rule via reflection: " + className, e);
        }
    }
}