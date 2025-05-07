package kr.money.book.analyze.web.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.analyze.web.domain.entity.BudgetAccountAnalyze;
import kr.money.book.analyze.web.domain.entity.BudgetAnalyze;
import kr.money.book.analyze.web.domain.entity.BudgetCategoryAnalyze;
import kr.money.book.analyze.web.domain.repository.BudgetAccountAnalyzeRepository;
import kr.money.book.analyze.web.domain.repository.BudgetAnalyzeRepository;
import kr.money.book.analyze.web.domain.repository.BudgetCategoryAnalyzeRepository;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.PersistenceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PersistenceTest
@CustomWithMockUser
public class AnalyzePersistenceTest {

    @Autowired
    private BudgetAnalyzeRepository budgetAnalyzeRepository;

    @Autowired
    private BudgetAccountAnalyzeRepository budgetAccountAnalyzeRepository;

    @Autowired
    private BudgetCategoryAnalyzeRepository budgetCategoryAnalyzeRepository;

    @Autowired
    private EntityManager entityManager;

    private String randomUserKey;
    private Long accountIdx1, accountIdx2;
    private Long categoryIdx1, categoryIdx2;

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
                    "VALUES (:userKey, 'Bank', 'BANK', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        accountIdx1 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_account (user_key, name, type, register_user, update_user) " +
                    "VALUES (:userKey, 'Card', 'CARD', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        accountIdx2 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_category (user_key, name, register_user, update_user) " +
                    "VALUES (:userKey, 'Income', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        categoryIdx1 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_category (user_key, name, register_user, update_user) " +
                    "VALUES (:userKey, 'Expense', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        categoryIdx2 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        BudgetAnalyze income = BudgetAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();
        BudgetAnalyze expense = BudgetAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx2)
            .categoryIdx(categoryIdx2)
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(500))
            .transactionDate(LocalDateTime.of(2025, 3, 2, 0, 0))
            .build();
        budgetAnalyzeRepository.saveAll(List.of(income, expense));

        BudgetAccountAnalyze account1 = BudgetAccountAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(1000))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetAccountAnalyze account2 = BudgetAccountAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(-500))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();
        budgetAccountAnalyzeRepository.saveAll(List.of(account1, account2));

        BudgetCategoryAnalyze category1 = BudgetCategoryAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(1000))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetCategoryAnalyze category2 = BudgetCategoryAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(500))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();
        budgetCategoryAnalyzeRepository.saveAll(List.of(category1, category2));
    }

    @AfterEach
    void tearDown() {
        budgetAnalyzeRepository.deleteByUserKey(randomUserKey);
        budgetAccountAnalyzeRepository.deleteByUserKey(randomUserKey);
        budgetCategoryAnalyzeRepository.deleteByUserKey(randomUserKey);
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
    void 예산분석저장_성공() {
        BudgetAnalyze budget = BudgetAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(2000))
            .transactionDate(LocalDateTime.of(2025, 3, 3, 0, 0))
            .build();
        BudgetAnalyze savedBudget = budgetAnalyzeRepository.save(budget);

        assertNotNull(savedBudget.getIdx());
        assertEquals(BudgetType.INCOME.name(), savedBudget.getType());
        assertEquals(BigDecimal.valueOf(2000), savedBudget.getAmount());
        assertEquals(categoryIdx1, savedBudget.getCategoryIdx());
    }

    @Test
    void 사용자키로예산목록찾기_성공() {
        List<BudgetAnalyze> budgets = budgetAnalyzeRepository.findByUserKeyAndTransactionDateBetweenAndAccountIdxIn(
            randomUserKey,
            LocalDateTime.of(2025, 3, 1, 0, 0),
            LocalDateTime.of(2025, 3, 31, 23, 59, 59),
            List.of(accountIdx1),
            null
        );
        assertEquals(1, budgets.size()); // accountIdx1에 해당하는 INCOME만 조회
        assertEquals(BudgetType.INCOME.name(), budgets.get(0).getType());
        assertEquals(BigDecimal.valueOf(1000), budgets.get(0).getAmount());
        assertEquals(categoryIdx1, budgets.get(0).getCategoryIdx());
    }

    @Test
    void 사용자키로삭제_성공() {
        budgetAnalyzeRepository.deleteByUserKey(randomUserKey);
        assertTrue(budgetAnalyzeRepository.findByUserKey(randomUserKey).isEmpty());
    }

    @Test
    void 사용자키로예산목록찾기_필터적용_성공() {
        List<BudgetAnalyze> budgets = budgetAnalyzeRepository.findByUserKeyAndTransactionDateBetweenAndAccountIdxIn(
            randomUserKey,
            LocalDateTime.of(2025, 3, 1, 0, 0),
            LocalDateTime.of(2025, 3, 31, 23, 59, 59),
            List.of(accountIdx2),
            List.of(categoryIdx2)
        );
        assertEquals(1, budgets.size());
        assertEquals(BudgetType.EXPENSE.name(), budgets.get(0).getType());
        assertEquals(BigDecimal.valueOf(500), budgets.get(0).getAmount());
    }
}
