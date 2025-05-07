package kr.money.book.analyze.web.infra;

import kr.money.book.analyze.web.exceptions.AnalysisException;
import kr.money.book.auth.redis.service.LoginCacheService;
import kr.money.book.common.valueobject.CacheInform;
import org.springframework.stereotype.Component;

@Component
public class AnalyzeAuthenticationService {

    private final LoginCacheService loginCacheService;

    public AnalyzeAuthenticationService(
        LoginCacheService loginCacheService) {

        this.loginCacheService = loginCacheService;
    }

    public CacheInform getCacheInform(String userKey) {

        CacheInform cacheInform = loginCacheService.get(userKey, CacheInform.class);
        if (cacheInform == null) {
            throw new AnalysisException(AnalysisException.ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return cacheInform;
    }

}
