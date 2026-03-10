package com.elias.riskcomplianceservice.domain.risk;

import java.math.BigDecimal;
import java.util.UUID;

public record RiskContext(
        UUID orderId,
        UUID investorId,
        UUID productId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount
) {}