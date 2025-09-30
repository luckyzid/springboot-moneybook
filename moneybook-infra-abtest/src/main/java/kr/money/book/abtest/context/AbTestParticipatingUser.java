package kr.money.book.abtest.context;

import java.util.Collections;
import java.util.Map;

public interface AbTestParticipatingUser {

    Long getAbTestUserId();

    default Map<String, Object> getAbTestAttributes() {
        return Collections.emptyMap();
    }
}
