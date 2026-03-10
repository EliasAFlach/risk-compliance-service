package com.elias.riskcomplianceservice.infrastructure.risk.gateways;

import com.elias.investcommon.domain.RiskDecisionType;
import com.elias.investcommon.event.risk.*;
import com.elias.investcommon.event.risk.OrderRiskApprovedEvent;
import com.elias.investcommon.event.risk.OrderRiskRejectedEvent;
import com.elias.investcommon.event.risk.OrderRiskReviewEvent;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskEventPublisherGateway;
import com.elias.riskcomplianceservice.domain.risk.RiskDecision;
import com.elias.riskcomplianceservice.domain.risk.RuleTrace;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRiskEventPublisher implements RiskEventPublisherGateway {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.risk-approved}")
    private String approvedTopic;

    @Value("${app.kafka.topics.risk-rejected}")
    private String rejectedTopic;

    @Value("${app.kafka.topics.risk-review}")
    private String reviewTopic;

    @Override
    public void publishDecision(RiskCheckRequestedEvent input, RiskDecision decision, UUID correlationId) {
        try {
            String key = input.getOrderId().toString();

            if (decision.decision() == RiskDecisionType.APPROVED) {
                OrderRiskApprovedEvent ev = OrderRiskApprovedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .occurredOn(Instant.now())
                        .schemaVersion("1")
                        .correlationId(correlationId)
                        .causationId(input.getEventId())
                        .orderId(input.getOrderId())
                        .investorId(input.getInvestorId())
                        .decision(RiskDecisionType.APPROVED)
                        .reasons(decision.reasons())
                        .ruleTrace(toDto(decision.ruleTrace()))
                        .build();

                kafkaTemplate.send(approvedTopic, key, objectMapper.writeValueAsString(ev));
                log.info("[RISK] Published APPROVED. orderId={} correlationId={}", input.getOrderId(), correlationId);
                return;
            }

            if (decision.decision() == RiskDecisionType.REJECTED) {
                OrderRiskRejectedEvent ev = OrderRiskRejectedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .occurredOn(Instant.now())
                        .schemaVersion("1")
                        .correlationId(correlationId)
                        .causationId(input.getEventId())
                        .orderId(input.getOrderId())
                        .investorId(input.getInvestorId())
                        .decision(RiskDecisionType.REJECTED)
                        .reasons(decision.reasons())
                        .ruleTrace(toDto(decision.ruleTrace()))
                        .build();

                kafkaTemplate.send(rejectedTopic, key, objectMapper.writeValueAsString(ev));
                log.info("[RISK] Published REJECTED. orderId={} correlationId={}", input.getOrderId(), correlationId);
                return;
            }

            // REVIEW
            OrderRiskReviewEvent ev = OrderRiskReviewEvent.builder()
                    .eventId(UUID.randomUUID())
                    .occurredOn(Instant.now())
                    .schemaVersion("1")
                    .correlationId(correlationId)
                    .causationId(input.getEventId())
                    .orderId(input.getOrderId())
                    .investorId(input.getInvestorId())
                    .decision(RiskDecisionType.REVIEW)
                    .reasons(decision.reasons())
                    .ruleTrace(toDto(decision.ruleTrace()))
                    .build();

            kafkaTemplate.send(reviewTopic, key, objectMapper.writeValueAsString(ev));
            log.info("[RISK] Published REVIEW. orderId={} correlationId={}", input.getOrderId(), correlationId);

        } catch (Exception e) {
            // Sem outbox: falhou publicar. Ainda assim você tem audit no banco.
            log.error("[RISK] Failed to publish decision event. orderId={}", input.getOrderId(), e);
            throw new RuntimeException("Failed to publish risk decision event", e);
        }
    }

    private List<RuleTraceDTO> toDto(List<RuleTrace> trace) {
        return trace.stream()
                .map(t -> RuleTraceDTO.builder()
                        .ruleKey(t.ruleKey())
                        .outcome(t.outcome().name())
                        .message(t.message())
                        .build())
                .toList();
    }
}