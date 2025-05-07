package kr.money.book.user.web.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import kr.money.book.common.constants.Role;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.PersistenceTest;
import kr.money.book.user.web.domain.entity.User;
import kr.money.book.user.web.domain.entity.UserToken;
import kr.money.book.user.web.domain.repository.UserRepository;
import kr.money.book.user.web.domain.repository.UserTokenRepository;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PersistenceTest
@CustomWithMockUser
public class UserPersistenceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTokenRepository userTokenRepository;

    private String randomEmail;
    private String randomUserKey;

    @BeforeEach
    void setUp() {
        randomEmail = StringUtil.generateRandomString(8) + "@example.com";
        randomUserKey = CustomWithMockUserSecurityContextFactory.getRandomUserKey();
    }

    @AfterEach
    void cleanUp() {
        userTokenRepository.deleteAll();
        userRepository.deleteAll();
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    void 사용자저장_성공() {
        User user = User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("User33")
            .build();
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getUserKey());
        assertEquals(Role.USER, savedUser.getRole());
        assertEquals(randomEmail, savedUser.getEmail());
    }

    @Test
    void 사용자키로사용자찾기_성공() {
        User user = User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("User123")
            .build();
        User savedUser = userRepository.save(user);

        User foundUser = userRepository.findByUserKey(savedUser.getUserKey()).orElse(null);
        assertNotNull(foundUser);
        assertEquals(savedUser.getUserKey(), foundUser.getUserKey());
        assertNotNull(foundUser.getRegisterDate());
        assertNotNull(foundUser.getUpdateDate());
    }

    @Test
    void 존재하지않는사용자찾기_널반환() {
        User foundUser = userRepository.findByUserKey(StringUtil.generateRandomString(10)).orElse(null);
        assertNull(foundUser);
    }

    @Test
    void 사용자와토큰저장_성공() {
        User user = User.builder()
            .email(randomEmail)
            .password("encoded")
            .name("PersistUser")
            .provider("email")
            .uniqueKey(randomEmail)
            .build();
        user = userRepository.save(user);

        UserToken token = UserToken.builder()
            .user(user)
            .token("persistToken")
            .isActive(1)
            .deviceInfo("device")
            .ipAddress("ip")
            .expirationTime(LocalDateTime.now().plusDays(1))
            .build();
        UserToken savedToken = userTokenRepository.save(token);

        assertTrue(userRepository.findByEmail(randomEmail).isPresent());
        assertTrue(userTokenRepository.findByToken("persistToken").isPresent());
        assertEquals(randomUserKey, savedToken.getRegisterUser());
        assertEquals(randomUserKey, savedToken.getUpdateUser());
    }

    @Test
    void 사용자차단_성공() {
        User user = User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("User33")
            .isBlocked(false)
            .build();
        User savedUser = userRepository.save(user);

        savedUser.block();
        userRepository.save(savedUser);

        User blockedUser = userRepository.findByUserKey(savedUser.getUserKey()).orElse(null);
        assertNotNull(blockedUser);
        assertTrue(blockedUser.getIsBlocked());
    }

    @Test
    void 사용자차단해제_성공() {
        User user = User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("User33")
            .isBlocked(true)
            .build();
        User savedUser = userRepository.save(user);

        savedUser.unblock();
        userRepository.save(savedUser);

        User unblockedUser = userRepository.findByUserKey(savedUser.getUserKey()).orElse(null);
        assertNotNull(unblockedUser);
        assertFalse(unblockedUser.getIsBlocked());
    }
}
