package kr.money.book.user.web.infra;

import io.jsonwebtoken.Claims;
import kr.money.book.auth.redis.service.AuthenticationRedisService;
import kr.money.book.auth.redis.service.LoginCacheService;
import kr.money.book.auth.redis.service.TokenCacheService;
import kr.money.book.auth.valueobject.AuthToken;
import kr.money.book.common.valueobject.CacheInform;
import kr.money.book.user.web.domain.mapper.UserInfoToCacheUserMapper;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationService {

    private final AuthenticationRedisService authenticationRedisService;
    private final LoginCacheService loginCacheService;
    private final TokenCacheService tokenCacheService;
    private final UserInfoToCacheUserMapper userInfoToCacheUserMapper;

    public UserAuthenticationService(
        AuthenticationRedisService authenticationRedisService,
        LoginCacheService loginCacheService,
        TokenCacheService tokenCacheService,
        UserInfoToCacheUserMapper userInfoToCacheUserMapper) {

        this.authenticationRedisService = authenticationRedisService;
        this.loginCacheService = loginCacheService;
        this.tokenCacheService = tokenCacheService;
        this.userInfoToCacheUserMapper = userInfoToCacheUserMapper;
    }

    public void saveAuthToken(String token, AuthToken authToken, long refreshTokenValidity) {

        tokenCacheService.set(token, authToken.refreshToken(), refreshTokenValidity);
    }

    public String getAuthToken(String token) {

        return tokenCacheService.get(token);
    }

    public void deleteAuthToken(String token) {

        tokenCacheService.del(token);
    }

    public Claims getClaimsWithThrow(String token) {

        return authenticationRedisService.getClaimsWithThrow(token);
    }

    public AuthToken generateAuthToken(String userKey, String role) {

        return authenticationRedisService.generateAuthToken(userKey, role);
    }

    public void clearAuthenticate() {

        authenticationRedisService.clearAuthenticate();
    }

    public void setAuthenticate(String userKey, String role) {

        authenticationRedisService.setAuthenticate(userKey, role);
    }

    public void clearCacheInform(String userKey) {

        loginCacheService.del(userKey);
    }

    public void syncCacheInform(UserInfo userInfo) {

        CacheInform cacheInform = loginCacheService.get(userInfo.userKey(), CacheInform.class);
        if (cacheInform == null) {
            cacheInform = CacheInform.builder().build();
        }

        cacheInform.setUser(userInfoToCacheUserMapper.map(userInfo));
        loginCacheService.set(userInfo.userKey(), cacheInform);
    }

}
