package kr.money.book.user.web.application.oauth.factory;

import java.util.Map;
import kr.money.book.user.web.domain.valueobject.OAuth2UserInfo;

public interface OAuth2LoginProvider {

    String getProvider();

    OAuth2UserInfo getOauth2UserInfo(Map<String, Object> attributes);
}
