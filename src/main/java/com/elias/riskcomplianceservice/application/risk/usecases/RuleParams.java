package com.elias.riskcomplianceservice.application.risk.usecases;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

public final class RuleParams {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RuleParams() {}

    public static Map<String, Object> parseJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap(); // MVP: params inválido não derruba pipeline
        }
    }
}