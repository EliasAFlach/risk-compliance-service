package com.elias.riskcomplianceservice.application.risk.usecases;

import com.elias.investcommon.domain.RiskDecisionType;
import com.elias.investcommon.event.risk.RiskCheckRequestedEvent;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskDecisionAuditRepositoryGateway;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskEventPublisherGateway;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleConfigDTO;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleConfigRepositoryGateway;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleFactoryGateway;
import com.elias.riskcomplianceservice.domain.risk.RiskContext;
import com.elias.riskcomplianceservice.domain.risk.RiskDecision;
import com.elias.riskcomplianceservice.domain.risk.RiskRule;
import com.elias.riskcomplianceservice.domain.risk.RiskRuleResult;
import com.elias.riskcomplianceservice.domain.risk.RuleOutcome;
import com.elias.riskcomplianceservice.domain.risk.RuleTrace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EvaluateRiskUseCase {

    private final RiskRuleConfigRepositoryGateway configRepo;
    private final RiskRuleFactoryGateway ruleFactory;
    private final RiskDecisionAuditRepositoryGateway auditRepo;
    private final RiskEventPublisherGateway eventPublisher;

    public EvaluateRiskUseCase(
            RiskRuleConfigRepositoryGateway configRepo,
            RiskRuleFactoryGateway ruleFactory,
            RiskDecisionAuditRepositoryGateway auditRepo,
            RiskEventPublisherGateway eventPublisher
    ) {
        this.configRepo = configRepo;
        this.ruleFactory = ruleFactory;
        this.auditRepo = auditRepo;
        this.eventPublisher = eventPublisher;
    }

    public void execute(RiskCheckRequestedEvent event) {
        UUID correlationId = event.getCorrelationId();

        RiskContext ctx = new RiskContext(
                event.getOrderId(),
                event.getInvestorId(),
                event.getProductId(),
                event.getQuantity(),
                event.getUnitPrice(),
                event.getTotalAmount()
        );

        List<RiskRuleConfigDTO> pipeline = configRepo.findEnabledOrdered();
        List<RuleTrace> trace = new ArrayList<>();
        List<String> reasons = new ArrayList<>();

        boolean hasReview = false;

        for (RiskRuleConfigDTO cfg : pipeline) {
            RiskRule rule = ruleFactory.create(cfg.className());
            Map<String, Object> params = RuleParams.parseJson(cfg.paramsJson());

            RiskRuleResult result = rule.evaluate(ctx, params);

            trace.add(new RuleTrace(
                    cfg.ruleKey(),
                    result.outcome(),
                    result.message()
            ));

            if (result.outcome() == RuleOutcome.FAIL) {
                if (result.message() != null) reasons.add(result.message());
                RiskDecision decision = new RiskDecision(RiskDecisionType.REJECTED, reasons, trace);
                auditRepo.saveAudit(ctx.orderId(), ctx.investorId(), correlationId, decision);
                eventPublisher.publishDecision(event, decision, correlationId);
                return;
            }

            if (result.outcome() == RuleOutcome.REVIEW) {
                hasReview = true;
                if (result.message() != null) reasons.add(result.message());
            }
        }

        RiskDecisionType finalDecision = hasReview ? RiskDecisionType.REVIEW : RiskDecisionType.APPROVED;
        RiskDecision decision = new RiskDecision(finalDecision, reasons, trace);

        auditRepo.saveAudit(ctx.orderId(), ctx.investorId(), correlationId, decision);
        eventPublisher.publishDecision(event, decision, correlationId);
    }
}