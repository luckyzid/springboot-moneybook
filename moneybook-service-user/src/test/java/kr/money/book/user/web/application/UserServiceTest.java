package kr.money.book.user.web.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import kr.money.book.common.constants.Role;
import kr.money.book.user.web.constants.Providers;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.exceptions.UserException;
import kr.money.book.user.web.infra.UserAuthenticationService;
import kr.money.book.user.web.infra.UserPersistenceAdapter;
import kr.money.book.user.web.infra.UserRedisPublishService;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserPersistenceAdapter userPersistenceAdapter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private UserRedisPublishService userRedisPublishService;

    private UserService userService;

    private UserInfo testUser;
    private String randomUserKey;
    private String randomEmail;

    @BeforeEach
    void setUp() {
        randomUserKey = StringUtil.generateRandomString(10);
        randomEmail = StringUtil.generateRandomString(8) + "@example.com";
        testUser = UserInfo.builder()
            .userKey(randomUserKey)
            .email(randomEmail)
            .name("TestUser")
            .provider(Providers.EMAIL.getType())
            .uniqueKey(randomEmail)
            .role(Role.USER)
            .build();
        userService = new UserService(
            passwordEncoder, tokenService,
            userPersistenceAdapter, userRedisPublishService, userAuthenticationService,
            "email.com,mail.com"
        );
    }

    @Test
    void 이메일로생성_성공() {
        when(userPersistenceAdapter.findByProviderAndEmail(Providers.EMAIL.getType(), randomEmail))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userPersistenceAdapter.saveUser(any(UserInfo.class))).thenReturn(testUser);

        UserInfo result = userService.createWithEmail(
            UserInfo.builder()
                .email(randomEmail)
                .password("password123")
                .name("TestUser")
                .build()
        );

        assertNotNull(result);
        assertEquals(randomEmail, result.email());
        verify(userPersistenceAdapter).saveUser(any(UserInfo.class));
    }

    @Test
    void 이메일로로그인_성공() {
        testUser = UserInfo.builder()
            .userKey(randomUserKey)
            .email(randomEmail)
            .name("TestUser")
            .provider(Providers.EMAIL.getType())
            .uniqueKey(randomEmail)
            .role(Role.USER)
            .password("encodedPassword")
            .isBlocked(false)
            .build();
        when(userPersistenceAdapter.findByProviderAndEmail(Providers.EMAIL.getType(), randomEmail))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        AuthTokenInfo authTokenInfo = AuthTokenInfo.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .userToken("userToken")
            .build();
        when(tokenService.generateTokens(testUser, "deviceInfo", "127.0.0.1")).thenReturn(authTokenInfo);
        doNothing().when(userRedisPublishService).sync(randomUserKey);
        doNothing().when(userAuthenticationService).setAuthenticate(anyString(), anyString());

        AuthTokenInfo result = userService.loginWithEmail(
            UserInfo.builder()
                .email(randomEmail)
                .password("password123")
                .userAgent("deviceInfo")
                .ipAddress("127.0.0.1")
                .isBlocked(false)
                .build()
        );

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        verify(userPersistenceAdapter).findByProviderAndEmail(Providers.EMAIL.getType(), randomEmail);
    }

    @Test
    void 이메일로생성_관리도메인_관리자역할할당() {
        String randomManageEmail = StringUtil.generateRandomString(8) + "@email.com";
        UserInfo inputUserInfo = UserInfo.builder()
            .email(randomManageEmail)
            .password("password123")
            .name("Test User")
            .build();
        UserInfo savedUserInfo = UserInfo.builder()
            .provider("email")
            .email(randomManageEmail)
            .password("encodedPassword")
            .name("Test User")
            .uniqueKey(randomManageEmail)
            .role(Role.MANAGE)
            .build();

        when(userPersistenceAdapter.findByProviderAndEmail("email", randomManageEmail))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userPersistenceAdapter.saveUser(any(UserInfo.class))).thenReturn(savedUserInfo);

        UserInfo result = userService.createWithEmail(inputUserInfo);

        assertEquals(Role.MANAGE, result.role());
        assertEquals(randomManageEmail, result.email());
        assertEquals("Test User", result.name());
    }

    @Test
    void 이메일로생성_일반도메인_사용자역할할당() {
        UserInfo inputUserInfo = UserInfo.builder()
            .email(randomEmail)
            .password("password123")
            .name("Test User")
            .build();
        UserInfo savedUserInfo = UserInfo.builder()
            .provider("email")
            .email(randomEmail)
            .password("encodedPassword")
            .name("Test User")
            .uniqueKey(randomEmail)
            .role(Role.USER)
            .build();

        when(userPersistenceAdapter.findByProviderAndEmail("email", randomEmail))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userPersistenceAdapter.saveUser(any(UserInfo.class))).thenReturn(savedUserInfo);

        UserInfo result = userService.createWithEmail(inputUserInfo);

        assertEquals(Role.USER, result.role());
        assertEquals(randomEmail, result.email());
        assertEquals("Test User", result.name());
    }

    @Test
    void 사용자정보조회_실패() {
        when(userPersistenceAdapter.findByUserKey(randomUserKey))
            .thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.getUserInfo(randomUserKey));
    }

    @Test
    void 이메일로그인_잘못된비밀번호() {
        testUser = UserInfo.builder()
            .userKey(randomUserKey)
            .email(randomEmail)
            .name("TestUser")
            .provider(Providers.EMAIL.getType())
            .uniqueKey(randomEmail)
            .role(Role.USER)
            .password("encodedPassword")
            .isBlocked(false)
            .build();
        when(userPersistenceAdapter.findByProviderAndEmail(Providers.EMAIL.getType(), randomEmail))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(UserException.class, () -> userService.loginWithEmail(
            UserInfo.builder()
                .email(randomEmail)
                .password("wrongPassword")
                .userAgent("deviceInfo")
                .ipAddress("127.0.0.1")
                .build()
        ));
    }

    @Test
    void 이메일로그인_차단된사용자() {
        testUser = UserInfo.builder()
            .userKey(randomUserKey)
            .email(randomEmail)
            .name("TestUser")
            .provider(Providers.EMAIL.getType())
            .uniqueKey(randomEmail)
            .role(Role.USER)
            .password("encodedPassword")
            .isBlocked(true)
            .build();
        when(userPersistenceAdapter.findByProviderAndEmail(Providers.EMAIL.getType(), randomEmail))
            .thenReturn(Optional.of(testUser));

        assertThrows(UserException.class, () -> userService.loginWithEmail(
            UserInfo.builder()
                .email(randomEmail)
                .password("password123")
                .userAgent("deviceInfo")
                .ipAddress("127.0.0.1")
                .build()
        ));
    }

    @Test
    void 사용자이름업데이트_실패() {
        when(userPersistenceAdapter.updateUserName(randomUserKey, "NewName"))
            .thenThrow(new UserException(UserException.ErrorCode.INVALID_USER_INFO));

        assertThrows(UserException.class, () -> userService.updateName(randomUserKey, "NewName"));
    }

    @Test
    void 비밀번호업데이트_잘못된현재비밀번호() {
        testUser = UserInfo.builder()
            .userKey(randomUserKey)
            .email(randomEmail)
            .name("TestUser")
            .provider(Providers.EMAIL.getType())
            .uniqueKey(randomEmail)
            .role(Role.USER)
            .password("encodedPassword")
            .build();
        when(userPersistenceAdapter.findByUserKey(randomUserKey))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(UserException.class, () -> userService.updatePassword(randomUserKey, "wrongPassword", "newPassword"));
    }

    @Test
    void 사용자차단_실패() {
        when(userPersistenceAdapter.findByUserKey(randomUserKey))
            .thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.blockUser(randomUserKey));
    }

    @Test
    void 사용자해제_실패() {
        when(userPersistenceAdapter.findByUserKey(randomUserKey))
            .thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.unblockUser(randomUserKey));
    }

    @Test
    void 로그아웃_성공() {
        doNothing().when(userAuthenticationService).clearCacheInform(randomUserKey);
        doNothing().when(userAuthenticationService).clearAuthenticate();

        userService.logout(randomUserKey);

        verify(userAuthenticationService).clearCacheInform(randomUserKey);
        verify(userAuthenticationService).clearAuthenticate();
    }

    @Test
    void 사용자삭제_성공() {
        doNothing().when(userAuthenticationService).clearCacheInform(randomUserKey);
        doNothing().when(userRedisPublishService).delete(randomUserKey);
        doNothing().when(userAuthenticationService).clearAuthenticate();
        doNothing().when(tokenService).deleteRefreshToken(randomUserKey);
        doNothing().when(userPersistenceAdapter).deleteUser(randomUserKey);

        userService.deleteUser(randomUserKey);

        verify(userAuthenticationService).clearCacheInform(randomUserKey);
        verify(userRedisPublishService).delete(randomUserKey);
        verify(userAuthenticationService).clearAuthenticate();
        verify(tokenService).deleteRefreshToken(randomUserKey);
        verify(userPersistenceAdapter).deleteUser(randomUserKey);
    }
}
