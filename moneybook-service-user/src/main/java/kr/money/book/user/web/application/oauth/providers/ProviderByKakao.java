package kr.money.book.user.web.application.oauth.providers;

import java.util.Map;
import kr.money.book.user.web.application.oauth.factory.OAuth2LoginProvider;
import kr.money.book.user.web.constants.Providers;
import kr.money.book.user.web.domain.valueobject.OAuth2UserInfo;
import org.springframework.stereotype.Component;

@Component
public class ProviderByKakao implements OAuth2LoginProvider {

    @Override
    public String getProvider() {
        return Providers.KAKAO.getType();
    }

    @Override
    public OAuth2UserInfo getOauth2UserInfo(Map<String, Object> attributes) {

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
            .name((String) profile.get("nickname"))
            .email((String) account.get("email"))
            .profile((String) profile.get("profile_image_url"))
            .provider("kakao")
            .uniqueKey(String.valueOf(attributes.get("id"))) // number
            .build();
    }
}
