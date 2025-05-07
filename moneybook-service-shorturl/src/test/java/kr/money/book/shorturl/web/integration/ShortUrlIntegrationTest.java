package kr.money.book.shorturl.web.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.IntegrationTest;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlCreateRequest;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlUpdateRequest;
import kr.money.book.shorturl.web.domain.repository.ShortUrlRepository;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
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
public class ShortUrlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String randomUserKey;

    @BeforeEach
    public void setUp() {
        shortUrlRepository.deleteAll();
        randomUserKey = CustomWithMockUserSecurityContextFactory.getRandomUserKey();
    }

    @AfterEach
    void tearDown() {
        shortUrlRepository.deleteAll();
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    @CustomWithMockUser(role = "ROLE_MANAGE")
    void 관리자단축URL_CRUD테스트() throws Exception {
        String originalUrl = "https://example.com/" + StringUtil.generateRandomString(10);
        LocalDateTime expireDate = LocalDateTime.now().plusDays(30);
        ShortUrlCreateRequest createRequest = new ShortUrlCreateRequest(originalUrl, expireDate);
        String createJson = objectMapper.writeValueAsString(createRequest);
        String createResponse = mockMvc.perform(post("/shorturl/manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.shortKey").exists())
            .andExpect(jsonPath("$.data.originalUrl", is(originalUrl)))
            .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<ShortUrlInfo> createApiResponse = objectMapper.readValue(createResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, ShortUrlInfo.class));
        String shortKey = createApiResponse.getData().shortKey();

        assertFalse(shortUrlRepository.findByShortKey(shortKey).isEmpty());

        mockMvc.perform(get("/shorturl/manager"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.lists[0].shortKey", is(shortKey)));

        String updatedUrl = "https://newexample.com/" + StringUtil.generateRandomString(10);
        LocalDateTime newExpireDate = LocalDateTime.now().plusDays(60);
        ShortUrlUpdateRequest updateRequest = new ShortUrlUpdateRequest(updatedUrl, newExpireDate);
        String updateJson = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(put("/shorturl/manager/" + shortKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.originalUrl", is(updatedUrl)));

        mockMvc.perform(get("/shorturl/manager/" + shortKey))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.shortKey", is(shortKey)));

        mockMvc.perform(delete("/shorturl/manager/" + shortKey))
            .andDo(print())
            .andExpect(status().isOk());

        assertTrue(shortUrlRepository.findByShortKey(shortKey).isEmpty());
    }

    @Test
    @CustomWithMockUser
    void 일반사용자_단축URL접근거부_테스트() throws Exception {
        String originalUrl = "https://example.com/" + StringUtil.generateRandomString(10);
        LocalDateTime expireDate = LocalDateTime.now().plusDays(30);
        ShortUrlCreateRequest createRequest = new ShortUrlCreateRequest(originalUrl, expireDate);
        String createJson = objectMapper.writeValueAsString(createRequest);
        mockMvc.perform(post("/shorturl/manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error.code", is("SERVER_0000")));
    }

    @Test
    @CustomWithMockUser(role = "ROLE_MANAGE")
    void 사용자단축URL_테스트() throws Exception {
        String originalUrl = "https://example.com/" + StringUtil.generateRandomString(10);
        LocalDateTime expireDate = LocalDateTime.now().plusDays(30);
        ShortUrlCreateRequest createRequest = new ShortUrlCreateRequest(originalUrl, expireDate);
        String createJson = objectMapper.writeValueAsString(createRequest);
        String createResponse = mockMvc.perform(post("/shorturl/manager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.shortKey").exists())
            .andExpect(jsonPath("$.data.originalUrl", is(originalUrl)))
            .andReturn().getResponse().getContentAsString();

        GlobalApiResponse<ShortUrlInfo> createApiResponse = objectMapper.readValue(createResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, ShortUrlInfo.class));
        String shortKey = createApiResponse.getData().shortKey();

        assertFalse(shortUrlRepository.findByShortKey(shortKey).isEmpty());

        mockMvc.perform(get("/" + shortKey))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(content().string(containsString(originalUrl)));

        assertFalse(shortUrlRepository.findByShortKey(shortKey).isEmpty());
    }
}
