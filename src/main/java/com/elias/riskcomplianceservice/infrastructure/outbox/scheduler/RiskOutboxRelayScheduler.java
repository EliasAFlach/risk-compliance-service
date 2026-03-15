package com.elias.riskcomplianceservice.infrastructure.outbox.scheduler;

import com.elias.riskcomplianceservice.infrastructure.outbox.persistence.RiskOutboxEntity;
import com.elias.riskcomplianceservice.infrastructure.outbox.persistence.SpringDataRiskOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RiskOutboxRelayScheduler {

    private final SpringDataRiskOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.outbox.relay-delay-ms:5000}")
    public void processOutboxMessages() {
        List<RiskOutboxEntity> pending = outboxRepository.findTop100ByProcessedAtIsNullOrderByCreatedAtAsc();

        if (pending.isEmpty()) {
            return;
        }

        for (RiskOutboxEntity msg : pending) {
            processSingle(msg);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingle(RiskOutboxEntity msg) {
        int attempts = msg.getAttempts() == null ? 1 : msg.getAttempts() + 1;
        msg.setAttempts(attempts);
        outboxRepository.save(msg);

        try {
            kafkaTemplate.send(msg.getTopic(), msg.getEventKey(), msg.getPayload()).get();

            msg.setProcessedAt(Instant.now());
            msg.setLastError(null);
            outboxRepository.save(msg);

            log.info("[RELAY] Enviado com sucesso. outboxId={} topic={} key={} attempts={}",
                    msg.getId(), msg.getTopic(), msg.getEventKey(), attempts);

        } catch (Exception ex) {
            msg.setLastError(truncate(ex.getMessage(), 500));
            outboxRepository.save(msg);

            log.warn("[RELAY] Falha ao enviar. outboxId={} topic={} key={} attempts={} erro={}",
                    msg.getId(), msg.getTopic(), msg.getEventKey(), attempts, ex.getMessage());
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}