package kr.money.book.account.web.application;

import java.util.List;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.account.web.exceptions.AccountException;
import kr.money.book.account.web.infra.AccountAuthenticationService;
import kr.money.book.account.web.infra.AccountPersistenceAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountPersistenceAdapter accountPersistenceAdapter;
    private final AccountAuthenticationService accountAuthenticationService;

    public AccountService(AccountPersistenceAdapter accountPersistenceAdapter,
        AccountAuthenticationService accountAuthenticationService) {
        this.accountPersistenceAdapter = accountPersistenceAdapter;
        this.accountAuthenticationService = accountAuthenticationService;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AccountInfo createAccount(AccountInfo accountInfo) {

        AccountInfo savedAccount = accountPersistenceAdapter.saveAccount(accountInfo);
        syncUserCache(accountInfo.userKey());

        return savedAccount;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AccountInfo updateAccount(AccountInfo accountInfo) {

        AccountInfo updatedAccount = accountPersistenceAdapter.updateAccount(accountInfo);
        syncUserCache(accountInfo.userKey());

        return updatedAccount;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<AccountInfo> getAccountList(String userKey) {

        return accountPersistenceAdapter.findAccountsByUserKey(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public AccountInfo getAccount(String userKey, Long accountIdx) {

        return accountPersistenceAdapter.findByUserKeyAndAccountIdx(userKey, accountIdx)
            .orElseThrow(() -> new AccountException(AccountException.ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteAccount(String userKey, Long accountIdx) {
        // Check if account exists
        AccountInfo account = accountPersistenceAdapter.findByUserKeyAndAccountIdx(userKey, accountIdx)
            .orElseThrow(() -> new AccountException(AccountException.ErrorCode.ACCOUNT_NOT_FOUND));

        accountPersistenceAdapter.deleteAccount(userKey, accountIdx);
        syncUserCache(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteAccount(String userKey) {

        accountPersistenceAdapter.deleteByUserKey(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public void syncUserCache(String userKey) {

        List<AccountInfo> accounts = accountPersistenceAdapter.findAccountsByUserKey(userKey);
        accountAuthenticationService.syncCacheInform(userKey, accounts);
    }
}
