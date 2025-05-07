package kr.money.book.user.web.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import kr.money.book.auth.exceptions.AuthException;
import kr.money.book.auth.redis.service.AuthenticationRedisService;
import kr.money.book.auth.redis.service.LoginCacheService;
import kr.money.book.common.constants.Role;
import kr.money.book.common.valueobject.CacheInform;
import kr.money.book.common.valueobject.CacheUser;
import kr.money.book.utils.JwtUtil;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class AuthenticationRedisServiceTest {

    @InjectMocks
    private AuthenticationRedisService authenticationRedisService;

    @Mock
    private LoginCacheService loginCacheService;

    @Mock
    private JwtUtil jwtUtil;

    private String randomUserKey;

    @BeforeEach
    void setUp() {
        randomUserKey = StringUtil.generateRandomString(10);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 캐시정보_가져오기_성공() {
        CacheUser cacheUser = CacheUser.builder()
            .userKey(randomUserKey)
            .role(Role.USER)
            .build();
        CacheInform userInform = CacheInform.builder().user(cacheUser).build();

        when(loginCacheService.get(randomUserKey, CacheInform.class)).thenReturn(userInform);

        CacheInform result = authenticationRedisService.getCacheInform(randomUserKey);

        assertNotNull(result);
        assertEquals(randomUserKey, result.getUser().getUserKey());
        verify(loginCacheService).get(randomUserKey, CacheInform.class);
    }

    @Test
    void 인증설정_유효한클레임_보안컨텍스트설정() {
        Claims claims = mock(Claims.class);
        when(claims.get("userId", String.class)).thenReturn(randomUserKey);
        when(claims.get("role", String.class)).thenReturn(Role.USER.name());
        CacheUser cacheUser = CacheUser.builder()
            .userKey(randomUserKey)
            .role(Role.USER)
            .build();
        CacheInform userInform = CacheInform.builder().user(cacheUser).build();

        when(loginCacheService.get(randomUserKey, CacheInform.class)).thenReturn(userInform);

        authenticationRedisService.setAuthenticate(claims);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(randomUserKey, auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.name())));
    }

    @Test
    void 인증설정_캐시에사용자정보없음_인증예외발생() {
        Claims claims = mock(Claims.class);
        String nonexistentKey = StringUtil.generateRandomString(10);
        when(claims.get("userId", String.class)).thenReturn(nonexistentKey);
        when(claims.get("role", String.class)).thenReturn(Role.USER.name());
        when(loginCacheService.get(nonexistentKey, CacheInform.class)).thenReturn(null);

        assertThrows(AuthException.class, () -> authenticationRedisService.setAuthenticate(claims));
    }

    @Test
    void 토큰검증_유효한토큰_참반환() {
        String token = "validToken";
        when(jwtUtil.validateToken(token)).thenReturn(true);

        boolean result = authenticationRedisService.validateToken(token);
        assertTrue(result);
        verify(jwtUtil, times(1)).validateToken(token);
    }

    @Test
    void 토큰검증_유효하지않은토큰_거짓반환() {
        String token = "invalidToken";
        when(jwtUtil.validateToken(token)).thenReturn(false);

        boolean result = authenticationRedisService.validateToken(token);
        assertFalse(result);
        verify(jwtUtil, times(1)).validateToken(token);
    }

    @Test
    void 클레임가져오기_유효한토큰_클레임반환() {
        String token = "validToken";
        Claims claims = mock(Claims.class);
        when(jwtUtil.getClaims(token)).thenReturn(claims);

        Claims result = authenticationRedisService.getClaims(token);
        assertEquals(claims, result);
        verify(jwtUtil, times(1)).getClaims(token);
    }

    @Test
    void 인증해제_보안컨텍스트초기화() {
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(randomUserKey, null)
        );
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        authenticationRedisService.clearAuthenticate();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
