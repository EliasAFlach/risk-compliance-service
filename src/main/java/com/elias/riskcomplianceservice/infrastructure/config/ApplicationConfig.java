package com.elias.riskcomplianceservice.infrastructure.config;

import com.elias.riskcomplianceservice.application.risk.gateways.RiskDecisionAuditRepositoryGateway;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskEventPublisherGateway;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleConfigRepositoryGateway;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleFactoryGateway;
import com.elias.riskcomplianceservice.application.risk.usecases.EvaluateRiskUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public EvaluateRiskUseCase evaluateRiskUseCase(
            RiskRuleConfigRepositoryGateway configRepo,
            RiskRuleFactoryGateway ruleFactory,
            RiskDecisionAuditRepositoryGateway auditRepo,
            RiskEventPublisherGateway publisher
    ) {
        return new EvaluateRiskUseCase(configRepo, ruleFactory, auditRepo, publisher);
    }
}