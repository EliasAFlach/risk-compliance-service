CREATE TABLE IF NOT EXISTS risk_rule_config (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_key    VARCHAR(255) NOT NULL UNIQUE,
    class_name  VARCHAR(500) NOT NULL,
    enabled     BOOLEAN NOT NULL DEFAULT true,
    order_index INTEGER NOT NULL,
    params_json TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS risk_decision_audit (
    id              UUID PRIMARY KEY,
    order_id        UUID NOT NULL,
    investor_id     UUID NOT NULL,
    decision        VARCHAR(50) NOT NULL,
    reasons_json    TEXT,
    rule_trace_json TEXT,
    correlation_id  UUID,
    created_at      TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS risk_tb_outbox (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic           VARCHAR(255) NOT NULL,
    event_key       VARCHAR(255) NOT NULL,
    payload         TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    processed_at    TIMESTAMPTZ,
    attempts        INTEGER DEFAULT 0,
    last_error      TEXT
);

-- Regras iniciais para teste
INSERT INTO risk_rule_config (rule_key, class_name, enabled, order_index, params_json)
VALUES
    (
        'max-total-amount',
        'com.elias.riskcomplianceservice.infrastructure.risk.rules.MaxTotalAmountRule',
        true,
        1,
        '{"max": "10000.00"}'
    ),
    (
        'suitability-review',
        'com.elias.riskcomplianceservice.infrastructure.risk.rules.SuitabilityMockReviewRule',
        true,
        2,
        '{"productId": "00000000-0000-0000-0000-000000000001"}'
    );