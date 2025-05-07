package kr.money.book.user.web.application.oauth;

import java.util.Map;
import kr.money.book.user.web.application.OAuth2UserService;
import kr.money.book.user.web.application.oauth.factory.OAuth2LoginFactory;
import kr.money.book.user.web.domain.valueobject.OAuth2UserInfo;
import kr.money.book.user.web.domain.valueobject.PrincipalDetails;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2CustomUserService extends DefaultOAuth2UserService {

    private final OAuth2LoginFactory oAuth2LoginFactory;
    private final OAuth2UserService oAuth2UserService;

    public OAuth2CustomUserService(OAuth2LoginFactory oAuth2LoginFactory, OAuth2UserService oAuth2UserService) {
        this.oAuth2LoginFactory = oAuth2LoginFactory;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        Map<String, Object> attributes = fetchAttributes(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oAuth2UserInfo = oAuth2LoginFactory
            .getOauth2LoginProvider(registrationId)
            .getOauth2UserInfo(attributes);

        UserInfo userInfo = oAuth2UserService.findOrSaveOAuth2User(oAuth2UserInfo);

        return new PrincipalDetails(userInfo, attributes, userNameAttributeName);
    }

    public Map<String, Object> fetchAttributes(OAuth2UserRequest userRequest) {

        return super.loadUser(userRequest).getAttributes();
    }
}
