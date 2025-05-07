package kr.money.book.category.web.infra;

import java.util.List;
import java.util.stream.Collectors;
import kr.money.book.auth.redis.service.LoginCacheService;
import kr.money.book.category.web.domain.mapper.CategoryInfoToCacheCategoryMapper;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.common.valueobject.CacheCategory;
import kr.money.book.common.valueobject.CacheInform;
import org.springframework.stereotype.Component;

@Component
public class CategoryAuthenticationService {

    private final LoginCacheService loginCacheService;
    private final CategoryInfoToCacheCategoryMapper categoryInfoToCacheCategoryMapper;

    public CategoryAuthenticationService(
        LoginCacheService loginCacheService,
        CategoryInfoToCacheCategoryMapper categoryInfoToCacheCategoryMapper) {

        this.loginCacheService = loginCacheService;
        this.categoryInfoToCacheCategoryMapper = categoryInfoToCacheCategoryMapper;
    }

    public void syncCacheInform(String userKey, List<CategoryInfo> categoryInfoList) {

        CacheInform cacheInform = loginCacheService.get(userKey, CacheInform.class);
        if (cacheInform == null) {
            return;
        }

        List<CacheCategory> collect = categoryInfoList.stream()
            .map(categoryInfoToCacheCategoryMapper::map)
            .collect(Collectors.toList());

        cacheInform.setCategories(collect);
        loginCacheService.set(userKey, cacheInform);
    }

}
