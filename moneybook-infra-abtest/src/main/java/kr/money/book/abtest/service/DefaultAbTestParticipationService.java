package kr.money.book.abtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import kr.money.book.abtest.allocator.AbTestVariantAllocator;
import kr.money.book.abtest.condition.AbTestCondition;
import kr.money.book.abtest.context.AbTestAssignment;
import kr.money.book.abtest.context.AbTestUserContext;
import kr.money.book.abtest.entity.AbTestEvent;
import kr.money.book.abtest.repository.AbTestEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultAbTestParticipationService implements AbTestParticipationService {

    private final AbTestEventRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void recordParticipation(AbTestAssignment assignment,
                                    Class<? extends AbTestCondition> conditionType,
                                    Class<? extends AbTestVariantAllocator> allocatorType) {

        AbTestEvent event = AbTestEvent.builder()
            .experimentKey(assignment.getExperimentKey())
            .variant(assignment.getVariant())
            .userId(assignment.getUserContext().getUserId())
            .conditionName(conditionType.getName())
            .allocatorName(allocatorType.getName())
            .attributesJson(writeAttributes(assignment.getUserContext().getAttributes()))
            .build();

        repository.save(event);
    }

    private String writeAttributes(Map<String, Object> attributes) {

        if (attributes == null || attributes.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize AB test attributes", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AbTestAssignment> findExistingAssignment(String experimentKey, AbTestUserContext userContext) {

        Long userId = userContext.getUserId();
        if (userId == null) {
            return Optional.empty();
        }

        return repository.findTopByExperimentKeyAndUserIdOrderByAssignedAtDesc(experimentKey, userId)
            .map(event -> AbTestAssignment.builder()
                .experimentKey(event.getExperimentKey())
                .variant(event.getVariant())
                .userContext(userContext)
                .assignedAt(event.getAssignedAt())
                .build());
    }
}
