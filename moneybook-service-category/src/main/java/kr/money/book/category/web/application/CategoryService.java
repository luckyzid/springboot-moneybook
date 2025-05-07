package kr.money.book.category.web.application;

import java.util.List;
import java.util.stream.Collectors;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.category.web.exceptions.CategoryException;
import kr.money.book.category.web.infra.CategoryAuthenticationService;
import kr.money.book.category.web.infra.CategoryPersistenceAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryPersistenceAdapter categoryPersistenceAdapter;
    private final CategoryAuthenticationService categoryAuthenticationService;

    public CategoryService(
        CategoryPersistenceAdapter categoryPersistenceAdapter,
        CategoryAuthenticationService categoryAuthenticationService) {

        this.categoryPersistenceAdapter = categoryPersistenceAdapter;
        this.categoryAuthenticationService = categoryAuthenticationService;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public CategoryInfo createCategory(final CategoryInfo categoryInfo) {
        // Check for duplicate category name
        List<CategoryInfo> existingCategories = categoryPersistenceAdapter.findCategoriesByUserKey(categoryInfo.userKey());
        boolean isDuplicate = existingCategories.stream()
            .anyMatch(c -> c.name().equals(categoryInfo.name()));
        if (isDuplicate) {
            throw new CategoryException(CategoryException.ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        int depth = calculateDepth(categoryInfo.parentIdx());
        CategoryInfo categoryInfoWithDepth = categoryInfo.toBuilder()
            .depth(depth)
            .build();
        CategoryInfo savedCategory = categoryPersistenceAdapter.saveCategory(categoryInfoWithDepth);
        syncUserCache(categoryInfo.userKey());

        return savedCategory;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public CategoryInfo updateCategory(final CategoryInfo categoryInfo) {
        // Check if category exists
        CategoryInfo existingCategory = categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(categoryInfo.userKey(), categoryInfo.idx())
            .orElseThrow(() -> new CategoryException(CategoryException.ErrorCode.CATEGORY_NOT_FOUND));

        // Check for duplicate category name if name is being changed
        if (!existingCategory.name().equals(categoryInfo.name())) {
            List<CategoryInfo> existingCategories = categoryPersistenceAdapter.findCategoriesByUserKey(categoryInfo.userKey());
            boolean isDuplicate = existingCategories.stream()
                .filter(c -> !c.idx().equals(categoryInfo.idx()))
                .anyMatch(c -> c.name().equals(categoryInfo.name()));
            if (isDuplicate) {
                throw new CategoryException(CategoryException.ErrorCode.DUPLICATE_CATEGORY_NAME);
            }
        }

        int depth = calculateDepth(categoryInfo.parentIdx());
        CategoryInfo categoryInfoWithDepth = categoryInfo.toBuilder()
            .depth(depth)
            .build();
        CategoryInfo updatedCategory = categoryPersistenceAdapter.updateCategory(categoryInfoWithDepth);
        syncUserCache(categoryInfo.userKey());

        return updatedCategory;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<CategoryInfo> getCategoryList(String userKey) {

        return categoryPersistenceAdapter.findCategoriesByUserKey(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public CategoryInfo getCategory(String userKey, Long categoryIdx) {

        return categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(userKey, categoryIdx)
            .orElseThrow(() -> new CategoryException(CategoryException.ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteCategory(String userKey, Long categoryIdx) {

        CategoryInfo foundCategoryInfo = categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(userKey, categoryIdx)
            .orElseThrow(() -> new CategoryException(CategoryException.ErrorCode.CATEGORY_NOT_FOUND));
        deleteCategoryWithChildren(userKey, foundCategoryInfo.idx());
        syncUserCache(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteCategory(String userKey) {

        categoryPersistenceAdapter.deleteByUserKey(userKey);
        syncUserCache(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public void syncUserCache(String userKey) {

        List<CategoryInfo> categories = categoryPersistenceAdapter.findCategoriesByUserKey(userKey);
        categoryAuthenticationService.syncCacheInform(userKey, categories);
    }

    private int calculateDepth(Long parentIdx) {

        if (parentIdx == null || parentIdx <= 0) {
            return 0;
        }

        return categoryPersistenceAdapter.findByIdx(parentIdx)
            .map(CategoryInfo::getDepth)
            .orElse(0) + 1;
    }

    private void deleteCategoryWithChildren(String userKey, Long categoryIdx) {

        List<CategoryInfo> foundCategories = categoryPersistenceAdapter.findCategoriesByUserKey(userKey).stream()
            .filter(c -> categoryIdx.equals(c.idx()) || isParentRoot(c, categoryIdx))
            .collect(Collectors.toList());

        categoryPersistenceAdapter.deleteAll(foundCategories);
    }

    private boolean isParentRoot(CategoryInfo category, Long rootIdx) {

        Long parentIdx = category.parentIdx();
        if (parentIdx == null) {
            return false;
        }

        if (parentIdx.equals(rootIdx)) {
            return true;
        }

        return categoryPersistenceAdapter.findByIdx(parentIdx)
            .map(parent -> isParentRoot(parent, rootIdx))
            .orElse(false);
    }

}
