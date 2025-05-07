package kr.money.book.auth.service;

import io.jsonwebtoken.Claims;
import kr.money.book.auth.valueobject.AuthToken;

public interface AuthenticationService {

    void setAuthenticate(Claims claims);

    void setAuthenticate(String userKey, String role);

    void clearAuthenticate();

    boolean validateToken(String token);

    Claims getClaims(String token);

    Claims getClaimsWithThrow(String token);

    AuthToken generateAuthToken(String userKey, String role);

}
