package kr.money.book.category.web.domain.mapper;

import kr.money.book.category.web.domain.entity.Category;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryInfoToCategoryMapper implements DomainMapper<CategoryInfo, Category> {

    @Override
    public Category map(CategoryInfo categoryInfo) {

        return Category.builder()
            .userKey(categoryInfo.userKey())
            .name(categoryInfo.name())
            .parentIdx(categoryInfo.parentIdx())
            .depth(categoryInfo.depth())
            .build();
    }
}
