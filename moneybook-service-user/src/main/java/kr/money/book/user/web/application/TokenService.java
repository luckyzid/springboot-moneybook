package kr.money.book.user.web.application;

import io.jsonwebtoken.Claims;
import kr.money.book.auth.valueobject.AuthToken;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.exceptions.UserException;
import kr.money.book.user.web.infra.UserAuthenticationService;
import kr.money.book.user.web.infra.UserPersistenceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TokenService {

    private final UserPersistenceAdapter userPersistenceAdapter;
    private final UserAuthenticationService userAuthenticationService;
    private final long userTokenExpiration;
    private final long refreshTokenValidity;

    public TokenService(
        UserPersistenceAdapter userPersistenceAdapter,
        UserAuthenticationService userAuthenticationService,
        @Value("${jwt.userTokenExpiration:2592000000}") long userTokenExpiration,
        @Value("${jwt.refreshTokenValidity:604800000}") long refreshTokenValidity) {

        this.userPersistenceAdapter = userPersistenceAdapter;
        this.userAuthenticationService = userAuthenticationService;
        this.userTokenExpiration = userTokenExpiration;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AuthTokenInfo generateTokens(UserInfo userInfo, String deviceInfo, String ipAddress) {

        if (userInfo == null) {
            throw new UserException(UserException.ErrorCode.INVALID_USER_INFO);
        }
        String userKey = userInfo.userKey();
        String role = userInfo.role().getKey();

        AuthToken authToken = userAuthenticationService.generateAuthToken(userKey, role);
        userAuthenticationService.saveAuthToken(userKey, authToken, refreshTokenValidity);

        String userToken = userPersistenceAdapter.generateUserTokenWithSave(
            userInfo,
            deviceInfo,
            ipAddress,
            userTokenExpiration
        );

        return AuthTokenInfo.create(authToken, userToken);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AuthTokenInfo renewToken(String refreshToken) {

        Claims claims = userAuthenticationService.getClaimsWithThrow(refreshToken);
        String userKey = claims.get("userId", String.class);
        String role = claims.get("role", String.class);

        AuthToken authToken = userAuthenticationService.generateAuthToken(userKey, role);

        String currentToken = userAuthenticationService.getAuthToken(userKey);
        if (currentToken != null && !currentToken.equals(refreshToken)) {
            throw new UserException(UserException.ErrorCode.INVALID_TOKEN_CREATE);
        }
        userAuthenticationService.saveAuthToken(userKey, authToken, refreshTokenValidity);

        String userToken = userPersistenceAdapter.findActiveTokenByUserKey(userKey);

        return AuthTokenInfo.create(authToken, userToken);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteRefreshToken(String userKey) {

        userAuthenticationService.deleteAuthToken(userKey);
    }
}
