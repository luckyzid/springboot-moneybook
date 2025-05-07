package kr.money.book.common.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    MANAGE("ROLE_MANAGE");

    private final String key;

    public static List<String> getRequiredRoles() {
        return Arrays.stream(Role.values())
            .map(Role::getKey)
            .collect(Collectors.toList());
    }
}
