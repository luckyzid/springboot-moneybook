package kr.money.book.user.web.domain.mapper;

import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.common.constants.Role;
import kr.money.book.user.web.constants.Providers;
import kr.money.book.user.web.domain.valueobject.OAuth2UserInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.exceptions.UserException;
import org.springframework.stereotype.Component;

@Component
public class OAuth2UserInfoToUserInfoMapper implements DomainMapper<OAuth2UserInfo, UserInfo> {

    @Override
    public UserInfo map(OAuth2UserInfo oAuth2UserInfo) {

        if (!Providers.isIncludeProviderByType(oAuth2UserInfo.provider())) {
            throw new UserException(UserException.ErrorCode.NOT_FOUND_PROVIDER);
        }

        return UserInfo.builder()
            .name(oAuth2UserInfo.name())
            .email(oAuth2UserInfo.email())
            .profile(oAuth2UserInfo.profile())
            .provider(oAuth2UserInfo.provider())
            .uniqueKey(oAuth2UserInfo.uniqueKey())
            .role(Role.USER)
            .build();
    }
}
