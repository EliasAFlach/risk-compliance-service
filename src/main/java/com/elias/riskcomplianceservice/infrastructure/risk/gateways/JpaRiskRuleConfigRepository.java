package com.elias.riskcomplianceservice.infrastructure.risk.gateways;

import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleConfigDTO;
import com.elias.riskcomplianceservice.application.risk.gateways.RiskRuleConfigRepositoryGateway;
import com.elias.riskcomplianceservice.infrastructure.risk.persistence.SpringDataRiskRuleConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaRiskRuleConfigRepository implements RiskRuleConfigRepositoryGateway {

    private final SpringDataRiskRuleConfigRepository repo;

    @Override
    public List<RiskRuleConfigDTO> findEnabledOrdered() {
        return repo.findByEnabledTrueOrderByOrderIndexAsc()
                .stream()
                .map(e -> new RiskRuleConfigDTO(
                        e.getRuleKey(),
                        e.getClassName(),
                        e.getOrderIndex(),
                        e.getParamsJson()
                ))
                .toList();
    }
}