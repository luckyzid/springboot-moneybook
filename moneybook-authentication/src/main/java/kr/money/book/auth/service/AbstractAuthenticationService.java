package kr.money.book.auth.service;

import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kr.money.book.auth.exceptions.AuthException;
import kr.money.book.auth.valueobject.AuthToken;
import kr.money.book.utils.JwtUtil;
import kr.money.book.utils.ShortUuid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
abstract public class AbstractAuthenticationService implements AuthenticationService {

    private final JwtUtil jwtUtil;

    protected AbstractAuthenticationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void setAuthenticate(Claims claims) {

        String userKey = claims.get("userId", String.class);
        String role = claims.get("role", String.class);
        setAuthenticate(userKey, role);
    }

    @Override
    public void setAuthenticate(String userKey, String role) {

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userKey,
            null,
            List.of(new SimpleGrantedAuthority(role))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public void clearAuthenticate() {

        SecurityContextHolder.clearContext();
    }

    @Override
    public boolean validateToken(String token) {

        return jwtUtil.validateToken(token);
    }

    @Override
    public Claims getClaims(String token) {

        return jwtUtil.getClaims(token);
    }

    @Override
    public Claims getClaimsWithThrow(String token) {

        if (!validateToken(token)) {
            throw new AuthException(AuthException.ErrorCode.INVALID_3TH_PART_TOKEN);
        }

        return getClaims(token);
    }

    @Override
    public AuthToken generateAuthToken(String userKey, String role) {

        String shortUuid = new ShortUuid.Builder()
            .build(UUID.randomUUID())
            .toString();

        Map<String, Object> claimsMap = Map.of(
            "userId", userKey,
            "role", role,
            "nonce", shortUuid // generate unique key
        );

        return AuthToken.builder()
            .accessToken(jwtUtil.generateAccessToken(claimsMap))
            .refreshToken(jwtUtil.generateRefreshToken(claimsMap))
            .build();
    }
}
