package kr.money.book.account.web.domain.command;

import java.util.Collections;
import java.util.Map;
import kr.money.book.abtest.context.AbTestParticipatingUser;

public record AccountListAbTestCommand(String userKey) implements AbTestParticipatingUser {

    public static AccountListAbTestCommand of(String userKey) {
        return new AccountListAbTestCommand(userKey);
    }

    @Override
    public Long getAbTestUserId() {

        if (userKey == null) {
            return null;
        }

        return Integer.toUnsignedLong(userKey.hashCode());
    }

    @Override
    public Map<String, Object> getAbTestAttributes() {

        if (userKey == null) {
            return Collections.emptyMap();
        }

        return Collections.singletonMap("userKey", userKey);
    }
}
