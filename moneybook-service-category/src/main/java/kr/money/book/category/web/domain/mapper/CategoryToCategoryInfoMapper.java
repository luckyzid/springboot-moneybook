package kr.money.book.category.web.domain.mapper;

import kr.money.book.category.web.domain.entity.Category;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryToCategoryInfoMapper implements DomainMapper<Category, CategoryInfo> {

    @Override
    public CategoryInfo map(Category category) {

        if (category == null) {
            return null;
        }

        return CategoryInfo.builder()
            .idx(category.getIdx())
            .userKey(category.getUserKey())
            .name(category.getName())
            .parentIdx(category.getParentIdx())
            .depth(category.getDepth())
            .build();
    }
}
