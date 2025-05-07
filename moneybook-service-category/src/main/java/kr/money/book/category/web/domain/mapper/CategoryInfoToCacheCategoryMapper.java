package kr.money.book.category.web.domain.mapper;

import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.common.valueobject.CacheCategory;
import org.springframework.stereotype.Component;

@Component
public class CategoryInfoToCacheCategoryMapper implements DomainMapper<CategoryInfo, CacheCategory> {

    @Override
    public CacheCategory map(CategoryInfo categoryInfo) {

        return CacheCategory.builder()
            .idx(categoryInfo.idx())
            .name(categoryInfo.name())
            .parentIdx(categoryInfo.parentIdx())
            .depth(categoryInfo.depth())
            .build();
    }
}
