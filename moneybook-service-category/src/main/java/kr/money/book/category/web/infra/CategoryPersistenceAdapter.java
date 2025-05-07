package kr.money.book.category.web.infra;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.money.book.category.web.domain.entity.Category;
import kr.money.book.category.web.domain.mapper.CategoryInfoToCacheCategoryMapper;
import kr.money.book.category.web.domain.mapper.CategoryInfoToCategoryMapper;
import kr.money.book.category.web.domain.mapper.CategoryToCategoryInfoMapper;
import kr.money.book.category.web.domain.repository.CategoryRepository;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.category.web.exceptions.CategoryException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CategoryPersistenceAdapter {

    private final CategoryRepository categoryRepository;
    private final CategoryInfoToCategoryMapper categoryInfoToCategoryMapper;
    private final CategoryToCategoryInfoMapper categoryToCategoryInfoMapper;

    public CategoryPersistenceAdapter(
        CategoryRepository categoryRepository,
        CategoryInfoToCategoryMapper categoryInfoToCategoryMapper,
        CategoryToCategoryInfoMapper categoryToCategoryInfoMapper) {

        this.categoryRepository = categoryRepository;
        this.categoryInfoToCategoryMapper = categoryInfoToCategoryMapper;
        this.categoryToCategoryInfoMapper = categoryToCategoryInfoMapper;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public CategoryInfo saveCategory(CategoryInfo categoryInfo) {

        Category savedCategory = categoryRepository.save(categoryInfoToCategoryMapper.map(categoryInfo));

        return categoryToCategoryInfoMapper.map(savedCategory);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public CategoryInfo updateCategory(CategoryInfo categoryInfo) {

        Category foundCategory = categoryRepository.findByIdx(categoryInfo.idx())
            .orElseThrow(() -> new CategoryException(CategoryException.ErrorCode.CATEGORY_NOT_FOUND));
        foundCategory.update(categoryInfo.name(), categoryInfo.parentIdx(), categoryInfo.depth());

        return categoryToCategoryInfoMapper.map(foundCategory);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public Optional<CategoryInfo> findByUserKeyAndCategoryIdx(String userKey, Long categoryIdx) {

        return categoryRepository.findByIdx(categoryIdx)
            .filter(c -> c.getUserKey().equals(userKey))
            .map(categoryToCategoryInfoMapper::map);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public Optional<CategoryInfo> findByIdx(Long idx) {

        return categoryRepository.findByIdx(idx).map(categoryToCategoryInfoMapper::map);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<CategoryInfo> findCategoriesByUserKey(String userKey) {

        return categoryRepository.findByUserKey(userKey).stream()
            .map(categoryToCategoryInfoMapper::map)
            .collect(Collectors.toList());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteByUserKey(String userKey) {

        categoryRepository.deleteByUserKey(userKey);
    }

    public void deleteAll(List<CategoryInfo> categories) {

        List<Category> collect = categories.stream()
            .map(categoryInfoToCategoryMapper::map)
            .collect(Collectors.toList());

        categoryRepository.deleteAll(collect);
    }
}
