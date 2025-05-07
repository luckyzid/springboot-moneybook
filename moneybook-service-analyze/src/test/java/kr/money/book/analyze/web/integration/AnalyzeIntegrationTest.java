package kr.money.book.analyze.web.integration;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import kr.money.book.common.constants.AnalysisType;
import kr.money.book.analyze.web.domain.datatransfer.AnalysisRequest;
import kr.money.book.analyze.web.domain.entity.BudgetAccountAnalyze;
import kr.money.book.analyze.web.domain.entity.BudgetAnalyze;
import kr.money.book.analyze.web.domain.entity.BudgetCategoryAnalyze;
import kr.money.book.analyze.web.domain.repository.BudgetAccountAnalyzeRepository;
import kr.money.book.analyze.web.domain.repository.BudgetAnalyzeRepository;
import kr.money.book.analyze.web.domain.repository.BudgetCategoryAnalyzeRepository;
import kr.money.book.analyze.web.infra.AnalyzeAuthenticationService;
import kr.money.book.common.constants.AccountType;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.common.valueobject.CacheAccount;
import kr.money.book.common.valueobject.CacheCategory;
import kr.money.book.common.valueobject.CacheInform;
import kr.money.book.common.valueobject.CacheUser;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@CustomWithMockUser
public class AnalyzeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BudgetAnalyzeRepository budgetAnalyzeRepository;

    @Autowired
    private BudgetAccountAnalyzeRepository budgetAccountAnalyzeRepository;

    @Autowired
    private BudgetCategoryAnalyzeRepository budgetCategoryAnalyzeRepository;

    @Autowired
    private AnalyzeAuthenticationService analyzeAuthenticationService;

    private String randomUserKey;
    private Long accountIdx1, accountIdx2, accountIdx3;
    private Long categoryIdx1, categoryIdx2;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AnalyzeAuthenticationService analyzeAuthenticationService() {
            return mock(AnalyzeAuthenticationService.class);
        }
    }

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
                    "VALUES (:userKey, 'A Card', 'CARD', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        accountIdx1 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_account (user_key, name, type, register_user, update_user) " +
                    "VALUES (:userKey, 'B Card', 'CARD', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        accountIdx2 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_account (user_key, name, type, register_user, update_user) " +
                    "VALUES (:userKey, 'A Bank', 'BANK', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        accountIdx3 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_category (user_key, name, register_user, update_user) " +
                    "VALUES (:userKey, '식비', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        categoryIdx1 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_category (user_key, name, register_user, update_user) " +
                    "VALUES (:userKey, '외식비', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        categoryIdx2 = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

        BudgetAnalyze budget1 = BudgetAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(9000))
            .comment("마트 장보기 + 추가 구매")
            .transactionDate(LocalDateTime.of(2025, 3, 1, 10, 0))
            .build();
        BudgetAnalyze budget2 = BudgetAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx2)
            .categoryIdx(categoryIdx1)
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(5000))
            .comment("편의점 간식")
            .transactionDate(LocalDateTime.of(2025, 3, 2, 15, 0))
            .build();
        BudgetAnalyze budget3 = BudgetAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx3)
            .categoryIdx(categoryIdx1)
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(12000))
            .comment("시장 장보기")
            .transactionDate(LocalDateTime.of(2025, 3, 3, 9, 0))
            .build();
        budgetAnalyzeRepository.saveAll(List.of(budget1, budget2, budget3));

        BudgetAccountAnalyze account1 = BudgetAccountAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(-9000))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(9000))
            .build();
        BudgetAccountAnalyze account2 = BudgetAccountAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(-5000))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(5000))
            .build();
        BudgetAccountAnalyze account3 = BudgetAccountAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx3)
            .transactionDate(LocalDate.of(2025, 3, 3))
            .amount(BigDecimal.valueOf(-12000))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(12000))
            .build();
        budgetAccountAnalyzeRepository.saveAll(List.of(account1, account2, account3));

        BudgetCategoryAnalyze category1 = BudgetCategoryAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(-9000))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(9000))
            .build();
        BudgetCategoryAnalyze category2 = BudgetCategoryAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx2)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(-5000))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(5000))
            .build();
        BudgetCategoryAnalyze category3 = BudgetCategoryAnalyze.builder()
            .userKey(randomUserKey)
            .accountIdx(accountIdx3)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 3))
            .amount(BigDecimal.valueOf(-12000))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(12000))
            .build();
        budgetCategoryAnalyzeRepository.saveAll(List.of(category1, category2, category3));

        entityManager.flush();
        entityManager.clear();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(
                CacheAccount.builder().idx(accountIdx1).name("A Card").type(AccountType.CARD.name()).build(),
                CacheAccount.builder().idx(accountIdx2).name("B Card").type(AccountType.CARD.name()).build(),
                CacheAccount.builder().idx(accountIdx3).name("A Bank").type(AccountType.BANK.name()).build()))
            .categories(List.of(
                CacheCategory.builder().idx(categoryIdx1).name("식비").build(),
                CacheCategory.builder().idx(categoryIdx2).name("외식비").build()))
            .build();
        when(analyzeAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);
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
    @Transactional
    void 주간분석_음식카테고리_성공() throws Exception {
        AnalysisRequest request = new AnalysisRequest(
            AnalysisType.WEEKLY,
            LocalDateTime.of(2025, 3, 1, 0, 0),
            Collections.emptyList(),
            List.of(categoryIdx1)
        );
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.startDate", is("2025-03-01 00:00:00")))
            .andExpect(jsonPath("$.data.endDate", is("2025-03-07 23:59:59")))
            .andExpect(jsonPath("$.data.analysisType", is("WEEKLY")))
            .andExpect(jsonPath("$.data.totalIncome", is(0.0)))
            .andExpect(jsonPath("$.data.totalExpense", is(26000.0)))
            .andExpect(jsonPath("$.data.categoryAnalysis.['" + categoryIdx1 + "'].totalIncome", is(0.0)))
            .andExpect(jsonPath("$.data.categoryAnalysis.['" + categoryIdx1 + "'].totalExpense", is(26000.0)))
            .andExpect(jsonPath("$.data.accountAnalysis.['" + accountIdx1 + "'].totalExpense", is(9000.0)))
            .andExpect(jsonPath("$.data.accountAnalysis.['" + accountIdx2 + "'].totalExpense", is(5000.0)))
            .andExpect(jsonPath("$.data.accountAnalysis.['" + accountIdx3 + "'].totalExpense", is(12000.0)))
            .andExpect(jsonPath("$.data.budgets.length()", is(3)));
    }

    @Test
    @Transactional
    void 주간분석_음식카테고리_A카드_성공() throws Exception {
        AnalysisRequest request = new AnalysisRequest(
            AnalysisType.WEEKLY,
            LocalDateTime.of(2025, 3, 1, 0, 0),
            List.of(accountIdx1),
            List.of(categoryIdx1)
        );
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.startDate", is("2025-03-01 00:00:00")))
            .andExpect(jsonPath("$.data.endDate", is("2025-03-07 23:59:59")))
            .andExpect(jsonPath("$.data.totalIncome", is(0.0)))
            .andExpect(jsonPath("$.data.totalExpense", is(9000.0)))
            .andExpect(jsonPath("$.data.categoryAnalysis.['" + categoryIdx1 + "'].totalExpense", is(9000.0)))
            .andExpect(jsonPath("$.data.accountAnalysis.['" + accountIdx1 + "'].totalExpense", is(9000.0)))
            .andExpect(jsonPath("$.data.budgets.length()", is(1)))
            .andExpect(jsonPath("$.data.budgets[0].comment", is("마트 장보기 + 추가 구매")));
    }
}
