CREATE TABLE IF NOT EXISTS risk_rule_config (
                                                id uuid PRIMARY KEY,
                                                rule_key varchar(120) NOT NULL,
    class_name varchar(500) NOT NULL,
    enabled boolean NOT NULL,
    order_index integer NOT NULL,
    params_json text,
    created_at timestamp NOT NULL,
    updated_at timestamp
    );

CREATE TABLE IF NOT EXISTS risk_decision_audit (
                                                   id uuid PRIMARY KEY,
                                                   order_id uuid NOT NULL,
                                                   investor_id uuid NOT NULL,
                                                   decision varchar(20) NOT NULL,
    reasons_json text,
    rule_trace_json text,
    correlation_id uuid,
    created_at timestamp NOT NULL
    );

-- Regra 1: rejeita se totalAmount > 10000
INSERT INTO risk_rule_config (id, rule_key, class_name, enabled, order_index, params_json, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     'MaxTotalAmountRule',
     'com.elias.riskservice.infrastructure.risk.rules.MaxTotalAmountRule',
     true,
     1,
     '{"max":"10000.00"}',
     NOW()
    )
    ON CONFLICT (id) DO NOTHING;

-- Regra 2: REVIEW se productId for o configurado (troque depois pelo seu productId real)
INSERT INTO risk_rule_config (id, rule_key, class_name, enabled, order_index, params_json, created_at)
VALUES
    ('22222222-2222-2222-2222-222222222222',
     'SuitabilityMockReviewRule',
     'com.elias.riskservice.infrastructure.risk.rules.SuitabilityMockReviewRule',
     true,
     2,
     '{"productId":"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"}',
     NOW()
    )
    ON CONFLICT (id) DO NOTHING;