package kr.money.book.abtest.context;

import java.util.Collections;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AbTestUserContext {

    private final Long userId;
    private final Map<String, Object> attributes;

    @Builder
    private AbTestUserContext(Long userId, Map<String, Object> attributes) {
        this.userId = userId;
        this.attributes = attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
    }

    public Object attribute(String key) {
        return attributes.get(key);
    }
}
