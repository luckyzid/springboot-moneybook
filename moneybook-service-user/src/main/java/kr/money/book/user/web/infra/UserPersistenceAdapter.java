package kr.money.book.user.web.infra;

import java.time.LocalDateTime;
import java.util.Optional;
import kr.money.book.common.constants.UserStatus;
import kr.money.book.rds.generators.UUIDGenerator;
import kr.money.book.user.web.domain.entity.User;
import kr.money.book.user.web.domain.entity.UserToken;
import kr.money.book.user.web.domain.mapper.UserInfoToUserMapper;
import kr.money.book.user.web.domain.mapper.UserToUserInfoMapper;
import kr.money.book.user.web.domain.mapper.UserTokenToUserTokenInfoMapper;
import kr.money.book.user.web.domain.repository.UserRepository;
import kr.money.book.user.web.domain.repository.UserTokenRepository;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.domain.valueobject.UserTokenInfo;
import kr.money.book.user.web.exceptions.UserException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserPersistenceAdapter {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserInfoToUserMapper userInfoToUserInfoMapper;
    private final UserToUserInfoMapper userToUserInfoMapper;
    private final UserTokenToUserTokenInfoMapper userTokenToUserTokenInfoMapper;

    public UserPersistenceAdapter(
        UserRepository userRepository,
        UserTokenRepository userTokenRepository,
        UserInfoToUserMapper userInfoToUserInfoMapper,
        UserToUserInfoMapper userToUserInfoMapper,
        UserTokenToUserTokenInfoMapper userTokenToUserTokenInfoMapper) {

        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.userInfoToUserInfoMapper = userInfoToUserInfoMapper;
        this.userToUserInfoMapper = userToUserInfoMapper;
        this.userTokenToUserTokenInfoMapper = userTokenToUserTokenInfoMapper;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public UserInfo saveUser(UserInfo userInfo) {

        User savedUser = userRepository.save(userInfoToUserInfoMapper.map(userInfo));

        return userToUserInfoMapper.map(savedUser);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public Optional<UserInfo> findByUserKey(String userKey) {

        return userRepository.findByUserKey(userKey).map(userToUserInfoMapper::map);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public Optional<UserInfo> findByProviderAndEmail(String provider, String email) {

        return userRepository.findByProviderAndEmail(provider, email).map(userToUserInfoMapper::map);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public Optional<UserTokenInfo> findUserTokenByToken(String token) {

        return userTokenRepository.findByToken(token).map(userTokenToUserTokenInfoMapper::map);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public String findActiveTokenByUserKey(String userKey) {

        return userTokenRepository.findTopByUser_UserKeyAndIsActiveOrderByUpdateDateDesc(userKey,
                UserStatus.ACTIVATE.getStatus())
            .map(UserToken::getToken)
            .orElse(null);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public UserInfo updateUserName(String userKey, String newName) {

        User foundUser = userRepository.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));
        foundUser.updateName(newName);

        return userToUserInfoMapper.map(foundUser);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public UserInfo updateUserPassword(String userKey, String newPassword) {

        User foundUser = userRepository.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));
        foundUser.updatePassword(newPassword);

        return userToUserInfoMapper.map(foundUser);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public String generateUserTokenWithSave(UserInfo userInfo, String deviceInfo, String ipAddress, long expire) {

        User foundUser = userRepository.findByUserKey(userInfo.userKey())
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));

        userTokenRepository.deactivateTokensByUserKey(foundUser.getUserKey());
        String tokenValue = UUIDGenerator.generateId();
        UserToken userToken = UserToken.builder()
            .user(foundUser)
            .token(tokenValue)
            .deviceInfo(deviceInfo)
            .ipAddress(ipAddress)
            .expirationTime(LocalDateTime.now().plusSeconds(expire / 1000))
            .isActive(UserStatus.ACTIVATE.getStatus())
            .build();

        userTokenRepository.save(userToken);

        return tokenValue;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void updateUserBlock(String userKey) {

        User user = userRepository.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));
        user.block();
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void updateUserUnblock(String userKey) {

        User user = userRepository.findByUserKey(userKey)
            .orElseThrow(() -> new UserException(UserException.ErrorCode.INVALID_USER_INFO));
        user.unblock();
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteUser(String userKey) {

        userRepository.deleteByUserKey(userKey);
        userTokenRepository.deleteByUser_UserKey(userKey);
    }
}
