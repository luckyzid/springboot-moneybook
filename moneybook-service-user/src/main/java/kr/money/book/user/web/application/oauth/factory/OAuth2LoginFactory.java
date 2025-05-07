package kr.money.book.user.web.application.oauth.factory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import kr.money.book.user.web.exceptions.UserException;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginFactory {

    private final Map<String, Supplier<OAuth2LoginProvider>> oAuth2Providers = new ConcurrentHashMap<>();

    public OAuth2LoginFactory(List<OAuth2LoginProvider> providers) {

        providers.forEach(provider -> {
            oAuth2Providers.put(provider.getProvider(), () -> provider);
        });
    }

    public OAuth2LoginProvider getOauth2LoginProvider(String providerKey) {

        if (!oAuth2Providers.containsKey(providerKey)) {
            throw new UserException(UserException.ErrorCode.NOT_FOUND_PROVIDER);
        }

        return oAuth2Providers.get(providerKey).get();
    }
}
