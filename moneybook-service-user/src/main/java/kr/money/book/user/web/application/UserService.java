package kr.money.book.user.web.application;

import java.util.Arrays;
import java.util.List;
import kr.money.book.common.constants.Role;
import kr.money.book.user.web.constants.Providers;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.domain.valueobject.UserTokenInfo;
import kr.money.book.user.web.exceptions.UserException;
import kr.money.book.user.web.exceptions.UserException.ErrorCode;
import kr.money.book.user.web.infra.UserAuthenticationService;
import kr.money.book.user.web.infra.UserPersistenceAdapter;
import kr.money.book.user.web.infra.UserRedisPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserPersistenceAdapter userPersistenceAdapter;
    private final UserRedisPublishService userRedisPublishService;
    private final UserAuthenticationService userAuthenticationService;
    private final List<String> manageDomains;

    public UserService(
        PasswordEncoder passwordEncoder,
        TokenService tokenService,
        UserPersistenceAdapter userPersistenceAdapter,
        UserRedisPublishService userRedisPublishService,
        UserAuthenticationService userAuthenticationService,
        @Value("${manage.domains:}") String manageDomains) {

        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.userRedisPublishService = userRedisPublishService;
        this.userAuthenticationService = userAuthenticationService;
        this.manageDomains = Arrays.asList(manageDomains.split(","));
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public UserInfo getUserInfo(String userKey) {

        UserInfo foundUserInfo = userPersistenceAdapter.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(ErrorCode.INVALID_USER_INFO));

        return foundUserInfo;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public UserInfo createWithEmail(UserInfo userInfo) {

        userPersistenceAdapter.findByProviderAndEmail(Providers.EMAIL.getType(), userInfo.email())
            .ifPresent(u -> {
                throw new UserException(UserException.ErrorCode.USER_ALREADY_EXISTS);
            });

        String emailDomain = userInfo.email().substring(userInfo.email().indexOf("@") + 1);
        Role role = manageDomains.contains(emailDomain) ? Role.MANAGE : Role.USER;

        UserInfo savedUserInfo = UserInfo.builder()
            .provider(Providers.EMAIL.getType())
            .email(userInfo.email())
            .password(passwordEncoder.encode(userInfo.password()))
            .name(userInfo.name())
            .uniqueKey(userInfo.email())
            .role(role)
            .build();

        return userPersistenceAdapter.saveUser(savedUserInfo);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AuthTokenInfo loginWithEmail(UserInfo userInfo) {

        UserInfo foundUserInfo = userPersistenceAdapter
            .findByProviderAndEmail(Providers.EMAIL.getType(), userInfo.email())
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));

        if (foundUserInfo.isBlocked()) {
            throw new UserException(UserException.ErrorCode.USER_BLOCKED);
        }

        if (!passwordEncoder.matches(userInfo.password(), foundUserInfo.password())) {
            throw new UserException(UserException.ErrorCode.INVALID_USER_INFO);
        }

        setUserInformAndAuth(foundUserInfo);

        return tokenService.generateTokens(foundUserInfo, userInfo.userAgent(), userInfo.ipAddress());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AuthTokenInfo loginWithUserToken(UserInfo userInfo) {

        UserTokenInfo foundUserInfo = userPersistenceAdapter.findUserTokenByToken(userInfo.token())
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_LOGIN_TOKEN));

        if (foundUserInfo.invalidActiveToken()
            || !foundUserInfo.matchesDeviceAndIp(userInfo.userAgent(), userInfo.ipAddress())) {
            throw new UserException(UserException.ErrorCode.INVALID_LOGIN_TOKEN);
        }

        UserInfo lastUserInfo = foundUserInfo.userInfo();
        if (lastUserInfo.isBlocked()) {
            throw new UserException(UserException.ErrorCode.USER_BLOCKED);
        }

        setUserInformAndAuth(lastUserInfo);

        return tokenService.generateTokens(lastUserInfo, userInfo.userAgent(), userInfo.ipAddress());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public UserInfo updateName(String userKey, String newName) {

        UserInfo updatedUserInfo = userPersistenceAdapter.updateUserName(userKey, newName);
        userAuthenticationService.syncCacheInform(updatedUserInfo);

        return updatedUserInfo;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public UserInfo updatePassword(String userKey, String currentPassword, String newPassword) {

        UserInfo foundUserInfo = userPersistenceAdapter.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));

        if (!passwordEncoder.matches(currentPassword, foundUserInfo.password())) {
            throw new UserException(UserException.ErrorCode.INVALID_CREDENTIALS);
        }

        return userPersistenceAdapter.updateUserPassword(userKey, passwordEncoder.encode(newPassword));
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void blockUser(String userKey) {

        userPersistenceAdapter.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));

        userPersistenceAdapter.updateUserBlock(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void unblockUser(String userKey) {

        userPersistenceAdapter.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));

        userPersistenceAdapter.updateUserUnblock(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void logout(String userKey) {

        userAuthenticationService.clearCacheInform(userKey);
        userAuthenticationService.clearAuthenticate();
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteUser(String userKey) {

        userAuthenticationService.clearCacheInform(userKey);
        userRedisPublishService.delete(userKey);
        userAuthenticationService.clearAuthenticate();
        tokenService.deleteRefreshToken(userKey);
        userPersistenceAdapter.deleteUser(userKey);
    }

    private void setUserInformAndAuth(UserInfo userInfo) {

        userAuthenticationService.syncCacheInform(userInfo);
        userRedisPublishService.sync(userInfo.userKey());
        userAuthenticationService.setAuthenticate(userInfo.userKey(), userInfo.role().getKey());
    }
}
