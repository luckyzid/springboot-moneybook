package kr.money.book.user.web.domain.mapper;

import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.user.web.domain.entity.User;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserInfoToUserMapper implements DomainMapper<UserInfo, User> {

    @Override
    public User map(UserInfo userInfo) {

        return User.builder()
            .userKey(userInfo.userKey())
            .provider(userInfo.provider())
            .email(userInfo.email())
            .uniqueKey(userInfo.uniqueKey())
            .password(userInfo.password())
            .name(userInfo.name())
            .profile(userInfo.profile())
            .role(userInfo.role())
            .isBlocked(userInfo.isBlocked())
            .build();
    }
}
