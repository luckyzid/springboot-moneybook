package kr.money.book.user.web.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.IntegrationTest;
import kr.money.book.user.web.domain.datatransfer.EmailCreateRequest;
import kr.money.book.user.web.domain.datatransfer.EmailLoginRequest;
import kr.money.book.user.web.domain.datatransfer.NameUpdateRequest;
import kr.money.book.user.web.domain.datatransfer.PasswordUpdateRequest;
import kr.money.book.user.web.domain.datatransfer.TokenRefreshRequest;
import kr.money.book.user.web.domain.repository.UserRepository;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import kr.money.book.utils.StringUtil;
import kr.money.book.web.response.GlobalApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@CustomWithMockUser
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private String randomUserKey;
    private String randomEmail;

    @BeforeEach
    public void setUp() {
        randomUserKey = CustomWithMockUserSecurityContextFactory.getRandomUserKey();
        randomEmail = StringUtil.generateRandomString(8) + "@example.com";
        userRepository.deleteByProviderAndEmail("email", randomEmail);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteByUserKey(randomUserKey);
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    void 회원가입_로그인_토큰갱신_로그아웃_수정_삭제_테스트() throws Exception {
        EmailCreateRequest emailCreateRequest = new EmailCreateRequest(randomEmail, "password123", "TestUser");
        String registerJson = objectMapper.writeValueAsString(emailCreateRequest);
        String registerResponse = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.email", is(randomEmail)))
            .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<UserInfo> registerApiResponse = objectMapper.readValue(registerResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, UserInfo.class));
        UserInfo registeredUser = registerApiResponse.getData();
        randomUserKey = registeredUser.userKey();

        EmailLoginRequest emailLoginRequest = new EmailLoginRequest(randomEmail, "password123");
        String loginJson = objectMapper.writeValueAsString(emailLoginRequest);
        String loginResponse = mockMvc.perform(post("/user/login/email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "TestAgent")
                .content(loginJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.accessToken", notNullValue()))
            .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<AuthTokenInfo> loginApiResponse = objectMapper.readValue(loginResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, AuthTokenInfo.class));
        AuthTokenInfo authTokenInfo = loginApiResponse.getData();

        PasswordUpdateRequest updateRequest = new PasswordUpdateRequest("password123", "newPassword456");
        String passwordUpdateJson = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(put("/user/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authTokenInfo.accessToken())
                .content(passwordUpdateJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.email", is(randomEmail)));

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest(authTokenInfo.refreshToken());
        String refreshJson = objectMapper.writeValueAsString(refreshRequest);
        String refreshResponse = mockMvc.perform(post("/user/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.refreshToken", not(authTokenInfo.refreshToken())))
            .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<AuthTokenInfo> refreshApiResponse = objectMapper.readValue(refreshResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, AuthTokenInfo.class));
        AuthTokenInfo refreshedAuthTokenInfo = refreshApiResponse.getData();

        NameUpdateRequest nameUpdateRequest = new NameUpdateRequest("UpdatedUser");
        String nameUpdateJson = objectMapper.writeValueAsString(nameUpdateRequest);
        mockMvc.perform(put("/user/name")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + refreshedAuthTokenInfo.accessToken())
                .content(nameUpdateJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.name", is("UpdatedUser")));

        mockMvc.perform(delete("/user")
                .header("Authorization", "Bearer " + refreshedAuthTokenInfo.accessToken()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)));
    }
}
