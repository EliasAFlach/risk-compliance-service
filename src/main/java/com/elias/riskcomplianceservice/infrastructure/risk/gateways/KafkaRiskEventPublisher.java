package com.elias.riskcomplianceservice.infrastructure.risk.gateways;

import com.elias.investcommon.event.risk.OrderRiskDecisionEvent;
import com.elias.investcommon.event.risk.RiskCheckRequestedEvent;
import com.elias.investcommon.event.risk.RuleTraceDTO;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskEventPublisherGateway;
import com.elias.riskcomplianceservice.domain.risk.RiskDecision;
import com.elias.riskcomplianceservice.domain.risk.RuleTrace;
import com.elias.riskcomplianceservice.infrastructure.outbox.persistence.RiskOutboxEntity;
import com.elias.riskcomplianceservice.infrastructure.outbox.persistence.SpringDataRiskOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRiskEventPublisher implements RiskEventPublisherGateway {

    private final SpringDataRiskOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.risk-approved}")
    private String approvedTopic;

    @Value("${app.kafka.topics.risk-rejected}")
    private String rejectedTopic;

    @Value("${app.kafka.topics.risk-review}")
    private String reviewTopic;

    @Override
    public void publishDecision(RiskCheckRequestedEvent input, RiskDecision decision, UUID correlationId) {
        String topic = resolveTopic(decision.decision());

        OrderRiskDecisionEvent event = OrderRiskDecisionEvent.builder()
                .eventId(UUID.randomUUID())
                .occurredOn(Instant.now())
                .schemaVersion("1")
                .correlationId(correlationId)
                .causationId(input.getEventId())
                .orderId(input.getOrderId())
                .investorId(input.getInvestorId())
                .decision(decision.decision())
                .reasons(decision.reasons())
                .ruleTrace(toDto(decision.ruleTrace()))
                .build();

        saveToOutbox(topic, input.getOrderId(), event);

        log.info("[OUTBOX] Decisão de risco salva. orderId={} decision={} correlationId={}",
                input.getOrderId(), decision.decision(), correlationId);
    }

    private String resolveTopic(com.elias.investcommon.domain.RiskDecisionType decision) {
        return switch (decision) {
            case APPROVED -> approvedTopic;
            case REJECTED -> rejectedTopic;
            case REVIEW   -> reviewTopic;
        };
    }

    private void saveToOutbox(String topic, UUID eventKey, Object event) {
        try {
            RiskOutboxEntity outbox = new RiskOutboxEntity();
            outbox.setTopic(topic);
            outbox.setEventKey(eventKey.toString());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setCreatedAt(Instant.now());
            outboxRepository.save(outbox);
        } catch (Exception e) {
            log.error("[OUTBOX] Falha ao salvar decisão de risco no outbox. topic={} eventKey={}", topic, eventKey, e);
            throw new RuntimeException("Erro ao salvar evento de risco no Outbox. topic=" + topic, e);
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