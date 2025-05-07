package kr.money.book.user.web.domain.mapper;

import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.user.web.domain.entity.User;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserToUserInfoMapper implements DomainMapper<User, UserInfo> {

    @Override
    public UserInfo map(User user) {

        if (user == null) {
            return null;
        }

        return UserInfo.builder()
            .userKey(user.getUserKey())
            .provider(user.getProvider())
            .email(user.getEmail())
            .uniqueKey(user.getUniqueKey())
            .password(user.getPassword())
            .name(user.getName())
            .profile(user.getProfile())
            .role(user.getRole())
            .isBlocked(user.getIsBlocked())
            .build();
    }
}
