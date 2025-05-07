package kr.money.book.user.web.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.util.List;
import kr.money.book.common.constants.Role;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.PersistenceTest;
import kr.money.book.user.web.domain.entity.User;
import kr.money.book.user.web.domain.repository.UserRepository;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@PersistenceTest
@CustomWithMockUser
public class UserEntityManagerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private String randomEmail;

    @BeforeEach
    void setUp() {
        randomEmail = StringUtil.generateRandomString(8) + "@example.com";
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteByEmail(randomEmail);
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    // persist: 데이터 생성 시 사용
    @Test
    void 영속화_새사용자생성() {
        User user = User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("NewUser")
            .build();

        // persist로 새로운 엔티티를 영속성 컨텍스트에 추가
        entityManager.persist(user);
        entityManager.flush(); // DB에 즉시 반영

        User foundUser = userRepository.findByUserKey(user.getUserKey()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("NewUser", foundUser.getName());
    }

    // merge: Detached 엔티티를 수정하고 다시 관리 상태로 만들 때 사용
    @Test
    void 병합_분리된사용자업데이트() {
        User user = userRepository.save(User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("OldName")
            .build());

        entityManager.detach(user); // Detached 상태로 전환
        user.updateName("NewName"); // Detached 상태에서 수정

        // merge로 수정된 엔티티를 다시 관리 상태로 병합
        User mergedUser = entityManager.merge(user);
        entityManager.flush();

        User foundUser = userRepository.findByUserKey(user.getUserKey()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("NewName", foundUser.getName());
    }

    // remove: 엔티티 삭제 시 사용
    @Test
    void 제거_사용자삭제() {
        User user = userRepository.save(User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("ToDelete")
            .build());

        entityManager.remove(entityManager.find(User.class, user.getIdx())); // 관리 상태에서 삭제
        entityManager.flush();

        User foundUser = userRepository.findByUserKey(user.getUserKey()).orElse(null);
        assertNull(foundUser);
    }

    // find: ID로 엔티티 조회 시 사용
    @Test
    void 찾기_ID로사용자검색() {
        User user = userRepository.save(User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("FindMe")
            .build());

        // find로 ID 기반 조회
        User foundUser = entityManager.find(User.class, user.getIdx());
        assertNotNull(foundUser);
        assertEquals("FindMe", foundUser.getName());
    }

    // flush: 트랜잭션 중간에 DB 동기화가 필요할 때 사용
    @Test
    void 플러시_변경사항즉시동기화() {
        User user = User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("FlushTest")
            .build();

        // 트랜잭션 없이 persist만 호출
        entityManager.persist(user);
        entityManager.clear(); // 영속성 컨텍스트 초기화

        // JPQL로 DB에서 직접 조회 (캐시 우회)
        User notYetSaved = entityManager.createQuery("SELECT u FROM User u WHERE u.uniqueKey = :key", User.class)
            .setParameter("key", user.getUserKey())
            .setHint("org.hibernate.readOnly", true)
            .getResultList()
            .stream().findFirst().orElse(null);
        assertNull(notYetSaved); // flush 전에는 DB에 없음

        // flush로 DB에 동기화
        entityManager.merge(user); // detached된 user를 다시 관리 상태로
        entityManager.flush();

        User savedUser = userRepository.findByUserKey(user.getUserKey()).orElse(null);
        assertNotNull(savedUser);
        assertEquals("FlushTest", savedUser.getName());
    }

    // clear: 캐시를 초기화해 최신 데이터를 가져올 때 사용
    @Test
    void 초기화_영속성컨텍스트리셋() {
        User user = userRepository.save(User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("BeforeClear")
            .build());

        User managedUser = entityManager.find(User.class, user.getIdx());
        managedUser.updateName("AfterClear"); // 영속성 컨텍스트에서 수정

        // clear로 영속성 컨텍스트 초기화
        entityManager.clear();
        User refreshedUser = entityManager.find(User.class, user.getIdx());
        assertEquals("BeforeClear", refreshedUser.getName()); // DB의 원래 값
    }

    // refresh: DB의 최신 상태를 엔티티에 반영할 때 사용
    @Test
    void 리프레시_데이터베이스에서엔티티업데이트() {
        User user = userRepository.save(User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("Original")
            .build());

        User managedUser = entityManager.find(User.class, user.getIdx());
        managedUser.updateName("Modified"); // 영속성 컨텍스트에서 수정

        // refresh로 DB 상태를 엔티티에 반영
        entityManager.refresh(managedUser);
        assertEquals("Original", managedUser.getName()); // DB의 원래 값으로 복원
    }

    // createQuery: 복잡한 조건으로 데이터를 조회할 때 사용
    @Test
    void 쿼리생성_조건으로사용자찾기() {
        userRepository.save(User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("User1")
            .build());
        userRepository.save(User.builder()
            .role(Role.USER)
            .email("other" + randomEmail)
            .provider("email")
            .uniqueKey("other" + randomEmail)
            .name("User2")
            .build());

        // createQuery로 복잡한 조건 조회
        List<User> users = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.provider = :provider AND u.name LIKE :namePattern",
                User.class)
            .setParameter("provider", "email")
            .setParameter("namePattern", "User%")
            .getResultList();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("User1")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("User2")));
    }

    // detach: 엔티티를 영속성 컨텍스트에서 분리해 더 이상 관리하지 않을 때 사용
    @Test
    void 분리_관리상태에서사용자제거() {
        User user = userRepository.save(User.builder()
            .role(Role.USER)
            .email(randomEmail)
            .provider("email")
            .uniqueKey(randomEmail)
            .name("DetachTest")
            .build());

        User managedUser = entityManager.find(User.class, user.getIdx());
        managedUser.updateName("Modified"); // 영속성 컨텍스트에서 수정
        entityManager.flush(); // DB에 반영
        entityManager.detach(managedUser); // 관리 상태에서 분리
        managedUser.updateName("DetachedChange"); // 분리된 상태에서 수정
        entityManager.flush(); // flush 해도 detached 엔티티는 반영되지 않음

        User dbUser = userRepository.findByUserKey(user.getUserKey()).orElse(null);
        assertNotNull(dbUser);
        assertEquals("Modified", dbUser.getName()); // DB에는 "Modified"가 반영됨
        assertEquals("DetachedChange", managedUser.getName()); // 로컬 객체는 변경됨
    }
}
