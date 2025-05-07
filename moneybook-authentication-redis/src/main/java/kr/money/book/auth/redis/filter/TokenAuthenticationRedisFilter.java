package kr.money.book.auth.redis.filter;

import kr.money.book.auth.filter.TokenAuthenticationFilter;
import kr.money.book.auth.service.AuthenticationService;

public class TokenAuthenticationRedisFilter extends TokenAuthenticationFilter {

    public TokenAuthenticationRedisFilter(AuthenticationService authenticationService) {
        super(authenticationService);
    }
}
