package kr.money.book.budget.web.infra;

import kr.money.book.auth.redis.service.LoginCacheService;
import kr.money.book.budget.web.exceptions.BudgetException;
import kr.money.book.common.valueobject.CacheInform;
import org.springframework.stereotype.Component;

@Component
public class BudgetAuthenticationService {

    private final LoginCacheService loginCacheService;

    public BudgetAuthenticationService(
        LoginCacheService loginCacheService) {

        this.loginCacheService = loginCacheService;
    }

    public CacheInform getCacheInform(String userKey) {

        CacheInform cacheInform = loginCacheService.get(userKey, CacheInform.class);
        if (cacheInform == null) {
            throw new BudgetException(BudgetException.ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return cacheInform;
    }

}
