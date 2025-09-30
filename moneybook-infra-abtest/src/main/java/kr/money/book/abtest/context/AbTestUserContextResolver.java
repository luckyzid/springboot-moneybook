package kr.money.book.abtest.context;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AbTestUserContextResolver {

    public static AbTestUserContext resolve(Object[] args) {

        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("AB test requires an argument that exposes AbTestUserContext");
        }

        return Arrays.stream(args)
            .map(AbTestUserContextResolver::mapToContext)
            .filter(context -> context != null && context.getUserId() != null)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No AbTestUserContext or AbTestParticipatingUser argument found"));
    }

    private static AbTestUserContext mapToContext(Object arg) {

        if (arg instanceof AbTestUserContext context) {
            return context;
        }

        if (arg instanceof AbTestParticipatingUser user) {
            return AbTestUserContext.builder()
                .userId(user.getAbTestUserId())
                .attributes(user.getAbTestAttributes())
                .build();
        }

        return null;
    }
}
