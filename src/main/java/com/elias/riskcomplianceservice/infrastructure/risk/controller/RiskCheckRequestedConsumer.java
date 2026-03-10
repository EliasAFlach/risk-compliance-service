package com.elias.riskcomplianceservice.infrastructure.risk.controller;

import com.elias.investcommon.event.risk.RiskCheckRequestedEvent;
import com.elias.riskcomplianceservice.application.risk.usecases.EvaluateRiskUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskCheckRequestedConsumer {

    private final ObjectMapper objectMapper;
    private final EvaluateRiskUseCase useCase;

    @KafkaListener(
            topics = "${app.kafka.topics.risk-check-requested}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(String payload) {
        try {
            RiskCheckRequestedEvent event = objectMapper.readValue(payload, RiskCheckRequestedEvent.class);
            log.info("[RISK] Received request. orderId={} correlationId={}", event.getOrderId(), event.getCorrelationId());
            useCase.execute(event);
        } catch (Exception e) {
            log.error("[RISK] Failed to process message", e);
        }
    }
}