package kr.money.book.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtUtil(
        @Value("${jwt.secretKey:T3M5RGubSI2EOB8ndnB5XnEu7RDtysdWPg4RlFi37JUSenVWITwBYXmgj52vnOnmbhaFYkXld3680WHB2sxs5o4GEIG4wdCEZlRyESyZGDoFkPkFs7W4F1hlA5PwdAs5tyYsrndILBDj}") String secretKey,
        @Value("${jwt.accessTokenValidity:900000}") long accessTokenValidity, // 15분
        @Value("${jwt.refreshTokenValidity:604800000}") long refreshTokenValidity // 7일
    ) {
        this.secretKey = getSecretKey(secretKey);
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    private SecretKey getSecretKey(String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Map<String, Object> claims) {
        return generateToken(claims, accessTokenValidity);
    }

    public String generateRefreshToken(Map<String, Object> claims) {
        return generateToken(claims, refreshTokenValidity);
    }

    private String generateToken(Map<String, Object> claims, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS512)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid token format: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Invalid signature: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("Token validation error: {}", e.getMessage());
        }
        return false;
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
