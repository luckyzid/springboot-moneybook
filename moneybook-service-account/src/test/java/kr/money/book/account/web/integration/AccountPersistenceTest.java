package kr.money.book.account.web.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.persistence.EntityManager;
import java.util.List;
import kr.money.book.common.constants.AccountType;
import kr.money.book.account.web.domain.entity.Account;
import kr.money.book.account.web.domain.repository.AccountRepository;
import kr.money.book.helper.CustomWithMockUser;
import kr.money.book.helper.CustomWithMockUserSecurityContextFactory;
import kr.money.book.helper.PersistenceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PersistenceTest
@CustomWithMockUser
public class AccountPersistenceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    private String randomUserKey;

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
    }

    @AfterEach
    void cleanUp() {
        accountRepository.deleteByUserKey(randomUserKey);
        CustomWithMockUserSecurityContextFactory.clearRandomUserKey();
    }

    @Test
    void 계좌저장_성공() {
        Account account = Account.builder()
            .userKey(randomUserKey)
            .name("TestBank")
            .type(AccountType.BANK)
            .build();
        Account savedAccount = accountRepository.save(account);

        assertNotNull(savedAccount.getIdx());
        assertEquals("TestBank", savedAccount.getName());
        assertNotNull(savedAccount.getRegisterDate());
    }

    @Test
    void 사용자키로계좌목록찾기_성공() {
        Account account = Account.builder()
            .userKey(randomUserKey)
            .name("TestBank")
            .type(AccountType.BANK)
            .build();
        accountRepository.save(account);

        List<Account> accounts = accountRepository.findByUserKey(randomUserKey);
        assertEquals(1, accounts.size());
        assertEquals("TestBank", accounts.get(0).getName());
    }
}
