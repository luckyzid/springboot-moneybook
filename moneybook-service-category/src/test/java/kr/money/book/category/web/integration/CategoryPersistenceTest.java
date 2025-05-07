package kr.money.book.category.web.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import kr.money.book.category.web.domain.entity.Category;
import kr.money.book.category.web.domain.repository.CategoryRepository;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.PersistenceTest;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@PersistenceTest
@CustomWithMockUser
public class CategoryPersistenceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private String randomUserKey;

    @BeforeEach
    void setUp() {
        randomUserKey = CustomWithMockUserSecurityContextFactory.getRandomUserKey();

        entityManager.createNativeQuery(
                "INSERT INTO mb_user (user_key, provider, unique_key, register_user, update_user, role) " +
                    "VALUES (:userKey, 'test', :uniqueKey, :registerUser, :updateUser, 'USER')"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("uniqueKey", randomUserKey + "_unique")
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
    }

    @AfterEach
    void cleanUp() {
        categoryRepository.deleteByUserKey(randomUserKey);
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    void 카테고리저장_성공() {
        String categoryName = "TestCategory_" + StringUtil.generateRandomString(5);
        Category category = Category.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .build();

        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory.getIdx());
        assertEquals(categoryName, savedCategory.getName());
        assertEquals(randomUserKey, savedCategory.getUserKey());
    }

    @Test
    void 사용자키로카테고리찾기_성공() {
        String categoryName = "TestCategory_" + StringUtil.generateRandomString(5);
        Category category = Category.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .build();
        categoryRepository.save(category);

        List<Category> categories = categoryRepository.findByUserKey(randomUserKey);
        assertEquals(1, categories.size());
        assertEquals(categoryName, categories.get(0).getName());
    }

    @Test
    void 사용자키로카테고리삭제_성공() {
        String categoryName = "TestCategory_" + StringUtil.generateRandomString(5);
        Category category = Category.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .build();
        categoryRepository.save(category);

        categoryRepository.deleteByUserKey(randomUserKey);
        assertTrue(categoryRepository.findByUserKey(randomUserKey).isEmpty());
    }

    @Test
    void 카테고리업데이트_성공() {
        String categoryName = "TestCategory_" + StringUtil.generateRandomString(5);
        Category category = Category.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .build();
        Category savedCategory = categoryRepository.save(category);

        String updatedName = "UpdatedCategory_" + StringUtil.generateRandomString(5);
        savedCategory.update(updatedName, null, 0);
        categoryRepository.save(savedCategory);

        Category updatedCategory = categoryRepository.findByIdx(savedCategory.getIdx()).get();
        assertEquals(updatedName, updatedCategory.getName());
    }

    @Test
    void 카테고리저장_부모포함_성공() {
        Category parent = Category.builder()
            .userKey(randomUserKey)
            .name("Parent_" + StringUtil.generateRandomString(5))
            .depth(0)
            .build();
        Category savedParent = categoryRepository.save(parent);

        Category child = Category.builder()
            .userKey(randomUserKey)
            .name("Child_" + StringUtil.generateRandomString(5))
            .parentIdx(savedParent.getIdx())
            .depth(1)
            .build();
        Category savedChild = categoryRepository.save(child);

        assertEquals(savedParent.getIdx(), savedChild.getParentIdx());
        assertEquals(1, savedChild.getDepth());
    }

    @Test
    void 카테고리저장_중복이름_예외발생() {
        String categoryName = "TestCategory_" + StringUtil.generateRandomString(5);
        Category category1 = Category.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .parentIdx(null)
            .build();
        categoryRepository.save(category1);

        Category category2 = Category.builder()
            .userKey(randomUserKey)
            .name(categoryName)
            .parentIdx(1l)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category2));
        entityManager.clear();
    }
}
