package kr.money.book.budget.web.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.entity.Budget;
import kr.money.book.budget.web.domain.repository.BudgetRepository;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.PersistenceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PersistenceTest
@CustomWithMockUser
public class BudgetPersistenceTest {
    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private EntityManager entityManager;

    private String randomUserKey;
    private Long accountIdx;
    private Long categoryIdx;

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

        entityManager.createNativeQuery(
                "INSERT INTO mb_account (user_key, name, type, register_user, update_user) " +
                    "VALUES (:userKey, 'TestAccount', 'BANK', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        accountIdx = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_category (user_key, name, register_user, update_user) " +
                    "VALUES (:userKey, 'TestCategory', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        categoryIdx = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();
    }

    @AfterEach
    void cleanUp() {
        budgetRepository.deleteByUserKey(randomUserKey);
        entityManager.createNativeQuery("DELETE FROM mb_account WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM mb_category WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM mb_user WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    void 예산저장_성공() {
        Budget budget = Budget.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .comment("Test Income")
            .transactionDate(LocalDateTime.now())
            .build();

        Budget savedBudget = budgetRepository.save(budget);

        assertNotNull(savedBudget.getIdx());
        assertEquals(randomUserKey, savedBudget.getUserKey());
        assertEquals(BigDecimal.valueOf(1000), savedBudget.getAmount());
    }

    @Test
    void 사용자키로예산찾기_성공() {
        Budget budget = Budget.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .type(BudgetType.EXPENSE)
            .amount(BigDecimal.valueOf(500))
            .comment("Test Expense")
            .transactionDate(LocalDateTime.now())
            .build();
        budgetRepository.save(budget);

        List<Budget> budgets = budgetRepository.findByUserKey(randomUserKey);
        assertEquals(1, budgets.size());
        assertEquals("Test Expense", budgets.get(0).getComment());
    }

    @Test
    void 사용자키로예산삭제_성공() {
        Budget budget = Budget.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.now())
            .build();
        budgetRepository.save(budget);

        budgetRepository.deleteByUserKey(randomUserKey);
        assertTrue(budgetRepository.findByUserKey(randomUserKey).isEmpty());
    }
}
