package kr.money.book.category.web.integration;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.List;
import kr.money.book.auth.redis.service.LoginCacheService;
import kr.money.book.category.web.domain.datatransfer.CategoryCreateRequest;
import kr.money.book.category.web.domain.datatransfer.CategoryInfoList;
import kr.money.book.category.web.domain.datatransfer.CategoryInfoListResponse;
import kr.money.book.category.web.domain.datatransfer.CategoryUpdateRequest;
import kr.money.book.category.web.domain.repository.CategoryRepository;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.IntegrationTest;
import kr.money.book.utils.StringUtil;
import kr.money.book.web.response.GlobalApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@CustomWithMockUser
public class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Mock
    private LoginCacheService loginCacheService;

    private String randomUserKey;

    @BeforeEach
    void setUp() {
        randomUserKey = CustomWithMockUserSecurityContextFactory.getRandomUserKey();
        categoryRepository.deleteByUserKey(randomUserKey);

        entityManager.createNativeQuery(
                "INSERT INTO mb_user (user_key, provider, unique_key, register_user, update_user, role) " +
                    "VALUES (:userKey, 'test', :uniqueKey, :registerUser, :updateUser, 'USER')"
            )
            .setParameter("userKey", randomUserKey)
            .setParameter("uniqueKey", randomUserKey + "_unique")
            .setParameter("registerUser", randomUserKey)
            .setParameter("updateUser", randomUserKey)
            .executeUpdate();
        entityManager.flush();
        entityManager.clear();

        Mockito.reset(loginCacheService);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteByUserKey(randomUserKey);
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    @Transactional
    void 카테고리CRUD_성공() throws Exception {
        String categoryName = "TestCategory_" + StringUtil.generateRandomString(5);
        CategoryCreateRequest createRequest = new CategoryCreateRequest(categoryName, null);
        String createJson = objectMapper.writeValueAsString(createRequest);

        String createResponse = mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.name", is(categoryName)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        GlobalApiResponse<CategoryInfo> createApiResponse = objectMapper.readValue(
            createResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, CategoryInfo.class)
        );
        Long categoryIdx = createApiResponse.getData().idx();

        mockMvc.perform(get("/category"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.lists[0].name", is(categoryName)));

        mockMvc.perform(get("/category/" + categoryIdx))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)));

        String updatedName = "UpdatedCategory_" + StringUtil.generateRandomString(5);
        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest(updatedName, null);
        String updateJson = objectMapper.writeValueAsString(updateRequest);
        mockMvc.perform(put("/category/" + categoryIdx)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.name", is(updatedName)));

        mockMvc.perform(delete("/category/" + categoryIdx))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @Transactional
    void 카테고리목록조회_성공() throws Exception {
        String categoryName1 = "Category1_" + StringUtil.generateRandomString(5);
        String categoryName2 = "Category2_" + StringUtil.generateRandomString(5);
        CategoryCreateRequest createRequest1 = new CategoryCreateRequest(categoryName1, null);
        CategoryCreateRequest createRequest2 = new CategoryCreateRequest(categoryName2, null);

        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest1)))
            .andDo(print())
            .andExpect(status().isOk());
        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest2)))
            .andDo(print())
            .andExpect(status().isOk());

        String response = mockMvc.perform(get("/category"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.lists.length()", is(2)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        GlobalApiResponse<CategoryInfoListResponse> apiResponse = objectMapper.readValue(
            response,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, CategoryInfoListResponse.class)
        );
        List<CategoryInfoList> categories = apiResponse.getData().lists();
        assertEquals(2, categories.size());
    }

    @Test
    @Transactional
    void 카테고리생성_부모포함_성공() throws Exception {
        String parentName = "ParentCategory_" + StringUtil.generateRandomString(5);
        CategoryCreateRequest parentRequest = new CategoryCreateRequest(parentName, null);
        String parentJson = objectMapper.writeValueAsString(parentRequest);
        String parentResponse = mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parentJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        GlobalApiResponse<CategoryInfo> createApiResponse = objectMapper.readValue(
            parentResponse,
            objectMapper.getTypeFactory().constructParametricType(GlobalApiResponse.class, CategoryInfo.class)
        );
        Long parentIdx = createApiResponse.getData().idx();

        String childName = "ChildCategory_" + StringUtil.generateRandomString(5);
        CategoryCreateRequest childRequest = new CategoryCreateRequest(childName, parentIdx);
        String childJson = objectMapper.writeValueAsString(childRequest);
        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(childJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.depth", is(1)))
            .andExpect(jsonPath("$.data.parentIdx", is(parentIdx.intValue())));
    }

    @Test
    @Transactional
    void 카테고리삭제_찾지못함_예외발생() throws Exception {
        mockMvc.perform(delete("/category/9999"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.error.code", is("CATEGORY_0001")));
    }
}
