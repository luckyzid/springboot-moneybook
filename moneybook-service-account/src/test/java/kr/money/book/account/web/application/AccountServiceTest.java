package kr.money.book.account.web.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kr.money.book.common.constants.AccountType;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.account.web.exceptions.AccountException;
import kr.money.book.account.web.infra.AccountAuthenticationService;
import kr.money.book.account.web.infra.AccountPersistenceAdapter;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Mock
    private AccountAuthenticationService accountAuthenticationService;

    @InjectMocks
    private AccountService accountService;

    private String randomUserKey;
    private String accountName;

    @BeforeEach
    void setUp() {
        randomUserKey = StringUtil.generateRandomString(10);
        accountName = "TestAccount_" + StringUtil.generateRandomString(5);
    }

    @Test
    void 계좌생성_성공() {
        AccountInfo testAccount = AccountInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(accountName)
            .type(AccountType.BANK)
            .build();

        when(accountPersistenceAdapter.saveAccount(any(AccountInfo.class))).thenReturn(testAccount);

        AccountInfo result = accountService.createAccount(
            AccountInfo.builder()
                .userKey(randomUserKey)
                .name(accountName)
                .type(AccountType.BANK)
                .build()
        );

        assertNotNull(result);
        assertEquals(accountName, result.name());
        assertEquals(randomUserKey, result.userKey());
    }

    @Test
    void 계좌업데이트_성공() {
        String updatedName = "UpdatedCategory_" + StringUtil.generateRandomString(5);

        AccountInfo accountInfo = AccountInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(updatedName)
            .type(AccountType.CARD)
            .build();
        when(accountPersistenceAdapter.updateAccount(any(AccountInfo.class))).thenReturn(accountInfo);

        AccountInfo result = accountService.updateAccount(
            AccountInfo.builder()
                .idx(1l)
                .userKey(randomUserKey)
                .name(updatedName)
                .type(AccountType.CARD)
                .build()
        );

        assertNotNull(result);
        assertEquals(updatedName, result.name());
        assertEquals(randomUserKey, result.userKey());
    }

    @Test
    void 계좌삭제_성공() {
        AccountInfo accountInfo = AccountInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(AccountType.BANK)
            .build();

        when(accountPersistenceAdapter.findByUserKeyAndAccountIdx(randomUserKey, 1l))
            .thenReturn(Optional.of(accountInfo));
        doNothing().when(accountPersistenceAdapter).deleteAccount(randomUserKey, 1l);

        accountService.deleteAccount(randomUserKey, 1l);
    }

    @Test
    void 계좌목록조회_성공() {
        AccountInfo testAccount = AccountInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .name(accountName)
            .type(AccountType.BANK)
            .build();

        when(accountPersistenceAdapter.findAccountsByUserKey(randomUserKey)).thenReturn(List.of(testAccount));

        List<AccountInfo> result = accountService.getAccountList(randomUserKey);

        assertEquals(1, result.size());
        assertEquals(accountName, result.get(0).name());
    }

    @Test
    void 계좌생성_사용자캐시동기화호출() {
        AccountInfo accountInfo = AccountInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(AccountType.BANK)
            .build();

        when(accountPersistenceAdapter.saveAccount(any(AccountInfo.class))).thenReturn(accountInfo);
        doNothing().when(accountAuthenticationService).syncCacheInform(eq(randomUserKey), any());

        accountService.createAccount(AccountInfo.builder()
            .userKey(randomUserKey)
            .type(AccountType.BANK)
            .build());

        verify(accountAuthenticationService).syncCacheInform(eq(randomUserKey), any());
    }

    @Test
    void 계좌조회_실패() {
        when(accountPersistenceAdapter.findByUserKeyAndAccountIdx(randomUserKey, 1l))
            .thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> accountService.getAccount(randomUserKey, 1l));
    }

    @Test
    void 계좌삭제_사용자캐시동기화호출() {
        AccountInfo accountInfo = AccountInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(AccountType.BANK)
            .build();

        when(accountPersistenceAdapter.findByUserKeyAndAccountIdx(randomUserKey, 1l))
            .thenReturn(Optional.of(accountInfo));
        doNothing().when(accountPersistenceAdapter).deleteAccount(randomUserKey, 1l);
        doNothing().when(accountAuthenticationService).syncCacheInform(eq(randomUserKey), any());

        accountService.deleteAccount(randomUserKey, 1l);

        verify(accountAuthenticationService).syncCacheInform(eq(randomUserKey), any());
    }

    @Test
    void 계좌삭제_실패() {
        when(accountPersistenceAdapter.findByUserKeyAndAccountIdx(randomUserKey, 1l))
            .thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> accountService.deleteAccount(randomUserKey, 1l));
    }

    @Test
    void 사용자별계좌전체삭제_성공() {
        doNothing().when(accountPersistenceAdapter).deleteByUserKey(randomUserKey);

        accountService.deleteAccount(randomUserKey);

        verify(accountPersistenceAdapter).deleteByUserKey(randomUserKey);
    }

    @Test
    void 계좌업데이트_사용자캐시동기화호출() {
        AccountInfo accountInfo = AccountInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(AccountType.BANK)
            .build();

        when(accountPersistenceAdapter.updateAccount(any(AccountInfo.class))).thenReturn(accountInfo);
        doNothing().when(accountAuthenticationService).syncCacheInform(eq(randomUserKey), any());

        accountService.updateAccount(AccountInfo.builder()
            .userKey(randomUserKey)
            .type(AccountType.BANK)
            .build());

        verify(accountAuthenticationService).syncCacheInform(eq(randomUserKey), any());
    }
}
