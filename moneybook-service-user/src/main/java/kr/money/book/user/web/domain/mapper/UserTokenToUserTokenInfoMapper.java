package kr.money.book.user.web.domain.mapper;

import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.user.web.domain.entity.UserToken;
import kr.money.book.user.web.domain.valueobject.UserTokenInfo;
import org.springframework.stereotype.Component;

@Component
public class UserTokenToUserTokenInfoMapper implements DomainMapper<UserToken, UserTokenInfo> {

    private final UserToUserInfoMapper userInfoToUserInfoMapper;

    public UserTokenToUserTokenInfoMapper(UserToUserInfoMapper userInfoToUserInfoMapper) {
        this.userInfoToUserInfoMapper = userInfoToUserInfoMapper;
    }

    @Override
    public UserTokenInfo map(UserToken userToken) {

        if (userToken == null) {
            return null;
        }

        return UserTokenInfo.builder()
            .idx(userToken.getIdx())
            .userInfo(userInfoToUserInfoMapper.map(userToken.getUser()))
            .token(userToken.getToken())
            .isActive(userToken.getIsActive())
            .deviceInfo(userToken.getDeviceInfo())
            .ipAddress(userToken.getIpAddress())
            .expirationTime(userToken.getExpirationTime())
            .build();
    }

}
