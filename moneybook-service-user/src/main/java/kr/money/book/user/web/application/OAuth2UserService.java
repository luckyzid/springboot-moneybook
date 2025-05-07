package kr.money.book.user.web.application;

import kr.money.book.user.web.domain.mapper.OAuth2UserInfoToUserInfoMapper;
import kr.money.book.user.web.domain.valueobject.OAuth2UserInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.user.web.infra.UserPersistenceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OAuth2UserService {

    private final UserPersistenceAdapter userPersistenceAdapter;
    private final OAuth2UserInfoToUserInfoMapper oAuth2UserInfoToUserInfoMapper;

    public OAuth2UserService(
        UserPersistenceAdapter userPersistenceAdapter,
        OAuth2UserInfoToUserInfoMapper oAuth2UserInfoToUserInfoMapper) {

        this.userPersistenceAdapter = userPersistenceAdapter;
        this.oAuth2UserInfoToUserInfoMapper = oAuth2UserInfoToUserInfoMapper;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public UserInfo findOrSaveOAuth2User(OAuth2UserInfo oAuth2UserInfo) {

        return userPersistenceAdapter.findByProviderAndEmail(oAuth2UserInfo.provider(), oAuth2UserInfo.email())
            .orElseGet(() -> {
                UserInfo newUser = oAuth2UserInfoToUserInfoMapper.map(oAuth2UserInfo);

                return userPersistenceAdapter.saveUser(newUser);
            });
    }
}
