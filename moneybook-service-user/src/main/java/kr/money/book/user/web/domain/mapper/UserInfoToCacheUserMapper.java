package kr.money.book.user.web.domain.mapper;

import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.common.valueobject.CacheUser;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserInfoToCacheUserMapper implements DomainMapper<UserInfo, CacheUser> {

    @Override
    public CacheUser map(UserInfo userInfo) {

        return CacheUser.builder()
            .userKey(userInfo.userKey())
            .provider(userInfo.provider())
            .email(userInfo.email())
            .uniqueKey(userInfo.uniqueKey())
            .profile(userInfo.profile())
            .role(userInfo.role())
            .ipAddress(userInfo.ipAddress())
            .userAgent(userInfo.userAgent())
            .build();
    }
}
