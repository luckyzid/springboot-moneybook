package kr.money.book.user.web.application.oauth.providers;

import java.util.Map;
import kr.money.book.user.web.application.oauth.factory.OAuth2LoginProvider;
import kr.money.book.user.web.constants.Providers;
import kr.money.book.user.web.domain.valueobject.OAuth2UserInfo;
import org.springframework.stereotype.Component;

@Component
public class ProviderByNaver implements OAuth2LoginProvider {

    @Override
    public String getProvider() {
        return Providers.NAVER.getType();
    }

    @Override
    public OAuth2UserInfo getOauth2UserInfo(Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
            .name((String) response.get("name"))
            .email((String) response.get("email"))
            .profile((String) response.get("profile_image"))
            .provider("naver")
            .uniqueKey((String) response.get("id"))
            .build();
    }
}
