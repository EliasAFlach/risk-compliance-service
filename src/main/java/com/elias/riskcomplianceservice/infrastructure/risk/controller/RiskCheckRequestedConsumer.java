package com.elias.riskcomplianceservice.infrastructure.risk.controller;

import com.elias.investcommon.event.risk.RiskCheckRequestedEvent;
import com.elias.riskcomplianceservice.application.risk.usecases.EvaluateRiskUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
        RiskCheckRequestedEvent event = null;
        try {
            event = objectMapper.readValue(payload, RiskCheckRequestedEvent.class);
            log.info("[RISK] Mensagem recebida. orderId={} correlationId={}",
                    event.getOrderId(), event.getCorrelationId());
            useCase.execute(event);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("[RISK] Payload inválido, descartando mensagem. payload={}", payload, e);
        } catch (Exception e) {
            UUID orderId = event != null ? event.getOrderId() : null;
            log.error("[RISK] Falha ao processar mensagem. orderId={}", orderId, e);
            throw new RuntimeException("Falha ao processar RiskCheckRequestedEvent. orderId=" + orderId, e);
        }
    }
}