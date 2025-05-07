package kr.money.book.auth.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.money.book.auth.constants.TokenKey;
import kr.money.book.auth.service.AuthenticationService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    public TokenAuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveToken(request);
        if (StringUtils.hasText(accessToken) && authenticationService.validateToken(accessToken)) {
            Claims claims = authenticationService.getClaims(accessToken);
            authenticationService.setAuthenticate(claims);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {

        String token = request.getHeader(TokenKey.AUTHORIZATION);

        return (StringUtils.hasText(token) && token.startsWith(TokenKey.TOKEN_PREFIX))
            ? token.substring(TokenKey.TOKEN_PREFIX.length())
            : null;
    }
}
