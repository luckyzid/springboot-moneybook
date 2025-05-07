package kr.money.book.budget.web.integration;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.common.constants.AccountType;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.datatransfer.BudgetCreateRequest;
import kr.money.book.budget.web.domain.datatransfer.BudgetUpdateRequest;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.budget.web.infra.BudgetAuthenticationService;
import kr.money.book.common.valueobject.CacheAccount;
import kr.money.book.common.valueobject.CacheCategory;
import kr.money.book.common.valueobject.CacheInform;
import kr.money.book.common.valueobject.CacheUser;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.IntegrationTest;
import kr.money.book.web.response.GlobalApiResponse;
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
public class BudgetIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BudgetAuthenticationService budgetAuthenticationService;

    private String randomUserKey;
    private Long accountIdx;
    private Long categoryIdx;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public BudgetAuthenticationService budgetAuthenticationService() {
            return mock(BudgetAuthenticationService.class);
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
                    "VALUES (:userKey, 'TestAccount', 'BANK', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        accountIdx = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()")
            .getSingleResult()).longValue();

        entityManager.createNativeQuery(
                "INSERT INTO mb_category (user_key, name, register_user, update_user) " +
                    "VALUES (:userKey, 'TestCategory1', :registerUser, :updateUser)"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        categoryIdx = ((Number) entityManager.createNativeQuery("SELECT LAST_INSERT_ID()")
            .getSingleResult()).longValue();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory1").build()))
            .build();
        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);

        entityManager.flush();
        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.createNativeQuery("DELETE FROM mb_budget_category WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM mb_budget WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM mb_account WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM mb_category WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM mb_user WHERE user_key = :userKey")
            .setParameter("userKey", randomUserKey)
            .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    @Transactional
    void 예산CRUD_테스트() throws Exception {
        BudgetCreateRequest createRequest = new BudgetCreateRequest(
            BudgetType.INCOME, BigDecimal.valueOf(1000), "Salary", LocalDateTime.now(), accountIdx, categoryIdx
        );
        String createJson = objectMapper.writeValueAsString(createRequest);
        String createResponse = mockMvc.perform(post("/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.amount", is(1000.0)))
            .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<BudgetInfo> createApiResponse = objectMapper.readValue(
            createResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, BudgetInfo.class)
        );
        Long budgetIdx = createApiResponse.getData().idx();

        mockMvc.perform(get("/budget"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.lists[0].comment", is("Salary")));

        mockMvc.perform(get("/budget/" + budgetIdx))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.amount", is(1000.0)));

        BudgetUpdateRequest updateRequest = new BudgetUpdateRequest(
            BudgetType.EXPENSE, BigDecimal.valueOf(500), "Updated Expense", LocalDateTime.now(), accountIdx, categoryIdx
        );
        String updateJson = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(put("/budget/" + budgetIdx)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.amount", is(500.0)));

        mockMvc.perform(delete("/budget/" + budgetIdx))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @Transactional
    void 예산삭제_찾지못함_예외발생() throws Exception {
        mockMvc.perform(delete("/budget/9999"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.error.code", is("BUDGET_0001")));
    }

    @Test
    @Transactional
    void 예산업데이트_카테고리정확히업데이트() throws Exception {
        BudgetCreateRequest createRequest = new BudgetCreateRequest(
            BudgetType.EXPENSE, BigDecimal.valueOf(500), "Initial", LocalDateTime.now(), accountIdx, categoryIdx
        );
        String createJson = objectMapper.writeValueAsString(createRequest);
        String createResponse = mockMvc.perform(post("/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<BudgetInfo> createApiResponse = objectMapper.readValue(
            createResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, BudgetInfo.class)
        );
        Long budgetIdx = createApiResponse.getData().idx();

        BudgetUpdateRequest updateRequest = new BudgetUpdateRequest(
            BudgetType.EXPENSE, BigDecimal.valueOf(600), "Updated", LocalDateTime.now(), accountIdx, categoryIdx
        );
        String updateJson = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(put("/budget/" + budgetIdx)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.amount", is(600.0)));
    }
}
