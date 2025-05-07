package kr.money.book.category.web.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.category.web.exceptions.CategoryException;
import kr.money.book.category.web.infra.CategoryAuthenticationService;
import kr.money.book.category.web.infra.CategoryPersistenceAdapter;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryPersistenceAdapter categoryPersistenceAdapter;

    @Mock
    private CategoryAuthenticationService categoryAuthenticationService;

    @InjectMocks
    private CategoryService categoryService;

    private String randomUserKey;
    private String categoryName;

    @BeforeEach
    void setUp() {
        randomUserKey = StringUtil.generateRandomString(10);
        categoryName = "TestCategory_" + StringUtil.generateRandomString(5);
    }

    @Test
    void 카테고리생성_성공() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        when(categoryPersistenceAdapter.saveCategory(any(CategoryInfo.class))).thenReturn(categoryInfo);

        CategoryInfo result = categoryService.createCategory(
            CategoryInfo.builder()
                .userKey(randomUserKey)
                .name(categoryName)
                .build()
        );

        assertNotNull(result);
        assertEquals(categoryName, result.name());
        assertEquals(randomUserKey, result.userKey());
    }

    @Test
    void 카테고리업데이트_성공() {
        String updatedName = "UpdatedCategory_" + StringUtil.generateRandomString(5);
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(updatedName)
            .build();

        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 1l))
            .thenReturn(Optional.of(categoryInfo));
        when(categoryPersistenceAdapter.updateCategory(any(CategoryInfo.class))).thenReturn(categoryInfo);

        CategoryInfo result = categoryService.updateCategory(
            CategoryInfo.builder()
                .idx(1l)
                .userKey(randomUserKey)
                .name(updatedName)
                .build()
        );

        assertNotNull(result);
        assertEquals(updatedName, result.name());
        assertEquals(randomUserKey, result.userKey());
    }

    @Test
    void 카테고리삭제_성공() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 1l)).thenReturn(Optional.of(categoryInfo));
        when(categoryPersistenceAdapter.findCategoriesByUserKey(randomUserKey)).thenReturn(List.of(categoryInfo));
        doNothing().when(categoryPersistenceAdapter).deleteAll(anyList());

        categoryService.deleteCategory(randomUserKey, 1l);

        verify(categoryPersistenceAdapter).deleteAll(anyList());
    }

    @Test
    void 카테고리목록조회_성공() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        when(categoryPersistenceAdapter.findCategoriesByUserKey(randomUserKey)).thenReturn(List.of(categoryInfo));

        List<CategoryInfo> result = categoryService.getCategoryList(randomUserKey);

        assertEquals(1, result.size());
        assertEquals(categoryName, result.get(0).name());
    }

    @Test
    void 카테고리생성_사용자캐시동기화호출() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        when(categoryPersistenceAdapter.saveCategory(any(CategoryInfo.class))).thenReturn(categoryInfo);
        doNothing().when(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());

        categoryService.createCategory(CategoryInfo.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .build());

        verify(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());
    }

    @Test
    void 카테고리생성_부모포함_깊이계산() {
        CategoryInfo parent = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name("Parent_" + StringUtil.generateRandomString(5))
            .depth(0)
            .build();
        CategoryInfo child = CategoryInfo.builder()
            .userKey(randomUserKey)
            .name("Child_" + StringUtil.generateRandomString(5))
            .parentIdx(1l)
            .build();

        when(categoryPersistenceAdapter.findByIdx(1l)).thenReturn(Optional.of(parent));
        when(categoryPersistenceAdapter.saveCategory(any(CategoryInfo.class))).thenAnswer(invocation -> {
            CategoryInfo info = invocation.getArgument(0);
            return info.toBuilder().idx(2l).depth(1).build();
        });

        CategoryInfo result = categoryService.createCategory(child);
        assertEquals(1, result.depth());
    }

    @Test
    void 카테고리삭제_자식포함_모두삭제() {
        CategoryInfo parent = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name("Parent_" + StringUtil.generateRandomString(5))
            .depth(0)
            .build();
        CategoryInfo child = CategoryInfo.builder()
            .idx(2l)
            .userKey(randomUserKey)
            .name("Child_" + StringUtil.generateRandomString(5))
            .parentIdx(1l)
            .depth(1)
            .build();

        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 1l)).thenReturn(Optional.of(parent));
        when(categoryPersistenceAdapter.findCategoriesByUserKey(randomUserKey)).thenReturn(List.of(parent, child));
        doNothing().when(categoryPersistenceAdapter).deleteAll(any());

        categoryService.deleteCategory(randomUserKey, 1l);
        verify(categoryPersistenceAdapter).deleteAll(ArgumentMatchers.argThat(list -> list.size() == 2));
    }

    @Test
    void 카테고리조회_찾지못함_예외발생() {
        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 999l)).thenReturn(Optional.empty());
        assertThrows(CategoryException.class, () -> categoryService.getCategory(randomUserKey, 999l));
    }

    @Test
    void 카테고리생성_중복된이름_예외발생() {
        CategoryInfo existingCategory = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        when(categoryPersistenceAdapter.findCategoriesByUserKey(randomUserKey))
            .thenReturn(List.of(existingCategory));

        assertThrows(CategoryException.class, () -> categoryService.createCategory(
            CategoryInfo.builder()
                .userKey(randomUserKey)
                .name(categoryName)
                .build()
        ));
    }

    @Test
    void 카테고리업데이트_존재하지않는카테고리_예외발생() {
        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 999l))
            .thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () -> categoryService.updateCategory(
            CategoryInfo.builder()
                .idx(999l)
                .userKey(randomUserKey)
                .name(categoryName)
                .build()
        ));
    }

    @Test
    void 카테고리업데이트_사용자캐시동기화호출() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 1l))
            .thenReturn(Optional.of(categoryInfo));
        when(categoryPersistenceAdapter.updateCategory(any(CategoryInfo.class))).thenReturn(categoryInfo);
        doNothing().when(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());

        categoryService.updateCategory(categoryInfo);

        verify(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());
    }

    @Test
    void 카테고리삭제_존재하지않는카테고리_예외발생() {
        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 999l))
            .thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () -> categoryService.deleteCategory(randomUserKey, 999l));
    }

    @Test
    void 사용자별카테고리전체삭제_성공() {
        doNothing().when(categoryPersistenceAdapter).deleteByUserKey(randomUserKey);
        doNothing().when(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());

        categoryService.deleteCategory(randomUserKey);

        verify(categoryPersistenceAdapter).deleteByUserKey(randomUserKey);
        verify(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());
    }

    @Test
    void 카테고리삭제_사용자캐시동기화호출() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        when(categoryPersistenceAdapter.findByUserKeyAndCategoryIdx(randomUserKey, 1l))
            .thenReturn(Optional.of(categoryInfo));
        when(categoryPersistenceAdapter.findCategoriesByUserKey(randomUserKey))
            .thenReturn(List.of(categoryInfo));
        doNothing().when(categoryPersistenceAdapter).deleteAll(anyList());
        doNothing().when(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());

        categoryService.deleteCategory(randomUserKey, 1l);

        verify(categoryAuthenticationService).syncCacheInform(eq(randomUserKey), any());
    }

    @Test
    void 카테고리생성_부모없음_깊이계산() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .parentIdx(null)
            .build();

        when(categoryPersistenceAdapter.saveCategory(any(CategoryInfo.class))).thenAnswer(invocation -> {
            CategoryInfo info = invocation.getArgument(0);
            return info.toBuilder().idx(1l).depth(0).build();
        });

        CategoryInfo result = categoryService.createCategory(categoryInfo);
        assertEquals(0, result.depth());
    }

    @Test
    void 카테고리생성_부모존재하지않음_깊이계산() {
        CategoryInfo categoryInfo = CategoryInfo.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .parentIdx(999l)
            .build();

        when(categoryPersistenceAdapter.findByIdx(999l)).thenReturn(Optional.empty());
        when(categoryPersistenceAdapter.saveCategory(any(CategoryInfo.class))).thenAnswer(invocation -> {
            CategoryInfo info = invocation.getArgument(0);
            return info.toBuilder().idx(1l).depth(0).build();
        });

        CategoryInfo result = categoryService.createCategory(categoryInfo);
        assertEquals(0, result.depth());
    }
}
