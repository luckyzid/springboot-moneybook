package kr.money.book.user.web.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import kr.money.book.user.web.application.oauth.OAuth2CustomUserService;
import kr.money.book.user.web.application.oauth.factory.OAuth2LoginFactory;
import kr.money.book.user.web.domain.valueobject.PrincipalDetails;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.exceptions.UserException;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OAuth2CustomUserServiceTest {

    @Mock
    private OAuth2LoginFactory oAuth2LoginFactory;

    @Mock
    private UserService userService;

    @Mock
    private OAuth2UserService oAuth2UserService;

    private OAuth2CustomUserService oAuth2CustomUserService;

    private OAuth2UserRequest userRequest;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ProviderDetails providerDetails = mock(ProviderDetails.class);
        UserInfoEndpoint userInfoEndpoint = mock(UserInfoEndpoint.class);

        lenient().when(clientRegistration.getRegistrationId()).thenReturn("google");
        lenient().when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        lenient().when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        lenient().when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("id");

        userRequest = mock(OAuth2UserRequest.class);
        lenient().when(userRequest.getClientRegistration()).thenReturn(clientRegistration);

        String randomGoogleId = StringUtil.generateRandomString(10);
        String randomEmail = StringUtil.generateRandomString(8) + "@test.com";
        attributes = new HashMap<>();
        attributes.put("id", randomGoogleId);
        attributes.put("name", "Test Google User");
        attributes.put("email", randomEmail);
        attributes.put("picture", "http://test.com/pic.jpg");

        oAuth2CustomUserService = spy(new OAuth2CustomUserService(oAuth2LoginFactory, oAuth2UserService));
    }

    @Test
    void OAuth2사용자로드_유효한사용자_PrincipalDetails반환() {
        String randomUserKey = StringUtil.generateRandomString(10);
        when(oAuth2LoginFactory.getOauth2LoginProvider("google"))
            .thenReturn(new kr.money.book.user.web.application.oauth.providers.ProviderByGoogle());
        UserInfo userInfo = UserInfo.builder()
            .userKey(randomUserKey)
            .email((String) attributes.get("email"))
            .name("Test Google User")
            .provider("google")
            .uniqueKey((String) attributes.get("id"))
            .build();
        when(oAuth2UserService.findOrSaveOAuth2User(any())).thenReturn(userInfo);
        doReturn(attributes).when(oAuth2CustomUserService).fetchAttributes(userRequest);

        OAuth2User oAuth2User = oAuth2CustomUserService.loadUser(userRequest);

        assertTrue(oAuth2User instanceof PrincipalDetails);
        PrincipalDetails principal = (PrincipalDetails) oAuth2User;
        assertTrue(principal.getUser().name().equals("Test Google User"));
        assertTrue(principal.getUser().email().equals(attributes.get("email")));
    }

    @Test
    void OAuth2사용자로드_유효하지않은공급자_사용자예외발생() {
        ClientRegistration invalidClientRegistration = mock(ClientRegistration.class);
        ProviderDetails invalidProviderDetails = mock(ProviderDetails.class);
        UserInfoEndpoint invalidUserInfoEndpoint = mock(UserInfoEndpoint.class);
        when(invalidClientRegistration.getRegistrationId()).thenReturn("unknown");
        when(invalidClientRegistration.getProviderDetails()).thenReturn(invalidProviderDetails);
        when(invalidProviderDetails.getUserInfoEndpoint()).thenReturn(invalidUserInfoEndpoint);
        when(invalidUserInfoEndpoint.getUserNameAttributeName()).thenReturn("id");

        OAuth2UserRequest invalidUserRequest = mock(OAuth2UserRequest.class);
        when(invalidUserRequest.getClientRegistration()).thenReturn(invalidClientRegistration);

        when(oAuth2LoginFactory.getOauth2LoginProvider("unknown"))
            .thenThrow(new UserException(UserException.ErrorCode.NOT_FOUND_PROVIDER));

        doReturn(attributes).when(oAuth2CustomUserService).fetchAttributes(invalidUserRequest);

        assertThrows(UserException.class, () -> oAuth2CustomUserService.loadUser(invalidUserRequest));
    }
}
