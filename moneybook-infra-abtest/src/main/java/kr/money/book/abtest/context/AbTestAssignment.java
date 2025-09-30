package kr.money.book.abtest.context;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AbTestAssignment {

    private final String experimentKey;
    private final String variant;
    private final AbTestUserContext userContext;
    private final LocalDateTime assignedAt;

    @Builder
    private AbTestAssignment(String experimentKey, String variant, AbTestUserContext userContext, LocalDateTime assignedAt) {
        this.experimentKey = experimentKey;
        this.variant = variant;
        this.userContext = userContext;
        this.assignedAt = assignedAt == null ? LocalDateTime.now() : assignedAt;
    }
}
