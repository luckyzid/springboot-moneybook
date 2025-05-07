package kr.money.book.user.web.constants;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Providers {
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google"),
    EMAIL("email");

    private final String type;

    public static boolean isIncludeProviderByType(String type) {

        return Arrays.stream(Providers.values())
            .anyMatch(providerType -> providerType.type.equals(type));
    }
}
