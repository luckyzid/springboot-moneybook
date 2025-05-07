package kr.money.book.account.web.infra;

import java.util.List;
import java.util.stream.Collectors;
import kr.money.book.account.web.domain.mapper.AccountInfoToCacheAccountMapper;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.auth.redis.service.LoginCacheService;
import kr.money.book.common.valueobject.CacheAccount;
import kr.money.book.common.valueobject.CacheInform;
import org.springframework.stereotype.Component;

@Component
public class AccountAuthenticationService {

    private final LoginCacheService loginCacheService;
    private final AccountInfoToCacheAccountMapper accountInfoToCacheAccountMapper;

    public AccountAuthenticationService(
        LoginCacheService loginCacheService,
        AccountInfoToCacheAccountMapper accountInfoToCacheAccountMapper) {

        this.loginCacheService = loginCacheService;
        this.accountInfoToCacheAccountMapper = accountInfoToCacheAccountMapper;
    }

    public void syncCacheInform(String userKey, List<AccountInfo> accounts) {

        CacheInform cacheInform = loginCacheService.get(userKey, CacheInform.class);
        if (cacheInform == null) {
            return;
        }

        List<CacheAccount> collect = accounts.stream()
            .map(accountInfoToCacheAccountMapper::map)
            .collect(Collectors.toList());

        cacheInform.setAccounts(collect);
        loginCacheService.set(userKey, cacheInform);
    }

}
