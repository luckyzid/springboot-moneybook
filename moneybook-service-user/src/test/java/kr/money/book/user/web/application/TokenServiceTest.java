package kr.money.book.user.web.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.money.book.auth.exceptions.AuthException;
import kr.money.book.auth.valueobject.AuthToken;
import kr.money.book.common.constants.Role;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.exceptions.UserException;
import kr.money.book.user.web.infra.UserAuthenticationService;
import kr.money.book.user.web.infra.UserPersistenceAdapter;
import kr.money.book.utils.JwtUtil;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    UserAuthenticationService userAuthenticationService;

    @Mock
    private UserPersistenceAdapter userPersistenceAdapter;

    private TokenService tokenService;

    private UserInfo testUser;
    private String randomUserKey;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(userPersistenceAdapter, userAuthenticationService,
            2592000000l,
            604800000l);

        randomUserKey = StringUtil.generateRandomString(10);
        testUser = UserInfo.builder()
            .userKey(randomUserKey)
            .email(StringUtil.generateRandomString(8) + "@test.com")
            .name("Test User")
            .provider("email")
            .uniqueKey(randomUserKey)
            .role(Role.USER)
            .build();
    }

    @Test
    void 토큰생성_유효한사용자_토큰쌍반환() {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String userToken = "userToken";
        AuthToken authToken = AuthToken.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();

        when(userAuthenticationService.generateAuthToken(testUser.userKey(), testUser.role().getKey()))
            .thenReturn(authToken);
        when(userPersistenceAdapter.generateUserTokenWithSave(eq(testUser), anyString(), anyString(), anyLong()))
            .thenReturn(userToken);

        AuthTokenInfo authTokenInfo = tokenService.generateTokens(testUser, "deviceInfo", "127.0.0.1");

        assertNotNull(authTokenInfo);
        assertEquals(accessToken, authTokenInfo.accessToken());
        assertEquals(refreshToken, authTokenInfo.refreshToken());
        assertEquals(userToken, authTokenInfo.userToken());
    }

    @Test
    void 토큰갱신_유효한토큰_새토큰반환() {
        String oldRefreshToken = "oldRefreshToken";
        Claims claims = Jwts.claims().add("userId", randomUserKey).add("role", Role.USER.name()).build();
        AuthToken authToken = AuthToken.builder()
            .accessToken("newAccessToken")
            .refreshToken("newRefreshToken")
            .build();

        when(userAuthenticationService.getClaimsWithThrow(oldRefreshToken)).thenReturn(claims);
        when(userAuthenticationService.generateAuthToken(randomUserKey, Role.USER.name())).thenReturn(authToken);
        when(userPersistenceAdapter.findActiveTokenByUserKey(randomUserKey)).thenReturn("existingUserToken");

        AuthTokenInfo authTokenInfo = tokenService.renewToken(oldRefreshToken);

        assertNotNull(authTokenInfo);
        assertEquals("newAccessToken", authTokenInfo.accessToken());
        assertEquals("newRefreshToken", authTokenInfo.refreshToken());
        assertEquals("existingUserToken", authTokenInfo.userToken());
    }

    @Test
    void 토큰갱신_유효하지않은토큰_사용자예외발생() {
        String invalidToken = "invalidToken";
        when(userAuthenticationService.getClaimsWithThrow(invalidToken))
            .thenThrow(new AuthException(AuthException.ErrorCode.INVALID_3TH_PART_TOKEN));

        assertThrows(AuthException.class, () -> tokenService.renewToken(invalidToken));
    }

    @Test
    void 토큰생성_유효하지않은사용자정보() {
        assertThrows(UserException.class, () -> tokenService.generateTokens(null, "deviceInfo", "127.0.0.1"));
    }

    @Test
    void 토큰갱신_유효하지않은현재토큰() {
        String oldRefreshToken = "oldRefreshToken";
        Claims claims = Jwts.claims().add("userId", randomUserKey).add("role", Role.USER.name()).build();
        AuthToken authToken = AuthToken.builder()
            .accessToken("newAccessToken")
            .refreshToken("newRefreshToken")
            .build();

        when(userAuthenticationService.getClaimsWithThrow(oldRefreshToken)).thenReturn(claims);
        when(userAuthenticationService.generateAuthToken(randomUserKey, Role.USER.name())).thenReturn(authToken);
        when(userAuthenticationService.getAuthToken(randomUserKey)).thenReturn("differentToken");

        assertThrows(UserException.class, () -> tokenService.renewToken(oldRefreshToken));
    }

    @Test
    void 리프레시토큰삭제_성공() {
        doNothing().when(userAuthenticationService).deleteAuthToken(randomUserKey);

        tokenService.deleteRefreshToken(randomUserKey);

        verify(userAuthenticationService).deleteAuthToken(randomUserKey);
    }
}
