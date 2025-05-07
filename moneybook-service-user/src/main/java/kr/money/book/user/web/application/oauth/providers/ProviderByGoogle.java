package kr.money.book.user.web.application.oauth.providers;

import java.util.Map;
import kr.money.book.user.web.application.oauth.factory.OAuth2LoginProvider;
import kr.money.book.user.web.constants.Providers;
import kr.money.book.user.web.domain.valueobject.OAuth2UserInfo;
import org.springframework.stereotype.Component;

@Component
public class ProviderByGoogle implements OAuth2LoginProvider {

    @Override
    public String getProvider() {
        return Providers.GOOGLE.getType();
    }

    @Override
    public OAuth2UserInfo getOauth2UserInfo(Map<String, Object> attributes) {

        return OAuth2UserInfo.builder()
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .profile((String) attributes.get("picture"))
            .provider("google")
            .uniqueKey((String) attributes.get("id"))
            .build();
    }
}
