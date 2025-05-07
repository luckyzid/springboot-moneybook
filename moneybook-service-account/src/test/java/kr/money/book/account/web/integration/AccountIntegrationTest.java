package kr.money.book.account.web.integration;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import kr.money.book.common.constants.AccountType;
import kr.money.book.account.web.domain.datatransfer.AccountCreateRequest;
import kr.money.book.account.web.domain.datatransfer.AccountUpdateRequest;
import kr.money.book.account.web.domain.repository.AccountRepository;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.IntegrationTest;
import kr.money.book.utils.StringUtil;
import kr.money.book.web.response.GlobalApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@CustomWithMockUser
public class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private String randomUserKey;

    @BeforeEach
    public void setUp() {
        randomUserKey = CustomWithMockUserSecurityContextFactory.getRandomUserKey();
        accountRepository.deleteByUserKey(randomUserKey);

        entityManager.createNativeQuery(
                        "INSERT INTO mb_user (user_key, provider, unique_key, register_user, update_user, role) " +
                                "VALUES (:userKey, 'test', :uniqueKey, :registerUser, :updateUser, 'USER')"
                )
                .setParameter("userKey", randomUserKey)
                .setParameter("uniqueKey", randomUserKey + "_unique")
                .setParameter("registerUser", randomUserKey)
                .setParameter("updateUser", randomUserKey)
                .executeUpdate();
        entityManager.clear();
        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteByUserKey(randomUserKey);
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    @Transactional
    void 계좌CRUD_테스트() throws Exception {
        String createAccountId = StringUtil.generateRandomString(10);
        AccountCreateRequest createRequest = new AccountCreateRequest(createAccountId, AccountType.BANK);
        String createJson = objectMapper.writeValueAsString(createRequest);
        String createResponse = mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is(createAccountId)))
                .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<AccountInfo> createApiResponse = objectMapper.readValue(createResponse,
                objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, AccountInfo.class));
        Long accountIdx = createApiResponse.getData().idx();

        mockMvc.perform(get("/account"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.lists[0].name", is(createAccountId)));

        mockMvc.perform(get("/account/" + accountIdx))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        AccountUpdateRequest updateRequest = new AccountUpdateRequest("UpdatedBank", AccountType.CARD);
        String updateJson = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(put("/account/" + accountIdx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("UpdatedBank")));

        mockMvc.perform(delete("/account/" + accountIdx))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
