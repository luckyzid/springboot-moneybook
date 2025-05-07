package kr.money.book.auth.redis.service;

import java.util.List;
import kr.money.book.auth.exceptions.AuthException;
import kr.money.book.auth.service.AbstractAuthenticationService;
import kr.money.book.common.valueobject.CacheInform;
import kr.money.book.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationRedisService extends AbstractAuthenticationService {

    private final LoginCacheService loginCacheService;

    public AuthenticationRedisService(JwtUtil jwtUtil, LoginCacheService loginCacheService) {
        super(jwtUtil);
        this.loginCacheService = loginCacheService;
    }

    @Override
    public void setAuthenticate(String userKey, String role) {

        CacheInform cacheInform = getCacheInform(userKey);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            cacheInform.getUser().getUserKey(),
            null,
            List.of(new SimpleGrantedAuthority(role))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public CacheInform getCacheInform(String userKey) {

        CacheInform cacheInform = loginCacheService.get(userKey, CacheInform.class);
        if (cacheInform == null) {
            throw new AuthException(AuthException.ErrorCode.INVALID_TOKEN);
        }

        return cacheInform;
    }

}
