package kr.money.book.account.web.infra;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.money.book.account.web.domain.entity.Account;
import kr.money.book.account.web.domain.mapper.AccountInfoToAccountMapper;
import kr.money.book.account.web.domain.mapper.AccountToAccountInfoMapper;
import kr.money.book.account.web.domain.repository.AccountRepository;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.account.web.exceptions.AccountException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountPersistenceAdapter {

    private final AccountRepository accountRepository;
    private final AccountInfoToAccountMapper accountInfoToAccountMapper;
    private final AccountToAccountInfoMapper accountToAccountInfoMapper;

    public AccountPersistenceAdapter
        (AccountRepository accountRepository,
        AccountInfoToAccountMapper accountInfoToAccountMapper,
            AccountToAccountInfoMapper accountToAccountInfoMapper) {

        this.accountRepository = accountRepository;
        this.accountInfoToAccountMapper = accountInfoToAccountMapper;
        this.accountToAccountInfoMapper = accountToAccountInfoMapper;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AccountInfo saveAccount(AccountInfo accountInfo) {

        Account savedAccount = accountRepository.save(accountInfoToAccountMapper.map(accountInfo));

        return accountToAccountInfoMapper.map(savedAccount);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public AccountInfo updateAccount(AccountInfo accountInfo) {

        Account foundAccount = accountRepository.findByIdx(accountInfo.idx())
            .filter(a -> a.getUserKey().equals(accountInfo.userKey()))
            .orElseThrow(() -> new AccountException(AccountException.ErrorCode.UNAUTHORIZED_ACCESS));
        foundAccount.updateAccount(accountInfo.name(), accountInfo.type());

        return accountToAccountInfoMapper.map(foundAccount);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteAccount(String userKey, Long accountIdx) {

        Account foundAccount = accountRepository.findByIdx(accountIdx)
            .filter(a -> a.getUserKey().equals(userKey))
            .orElseThrow(() -> new AccountException(AccountException.ErrorCode.UNAUTHORIZED_ACCESS));

        accountRepository.delete(foundAccount);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<AccountInfo> findAccountsByUserKey(String userKey) {

        return accountRepository.findByUserKey(userKey).stream()
            .map(accountToAccountInfoMapper::map)
            .collect(Collectors.toList());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public Optional<AccountInfo> findByUserKeyAndAccountIdx(String userKey, Long accountIdx) {

        return accountRepository.findByIdx(accountIdx)
            .filter(a -> a.getUserKey().equals(userKey))
            .map(accountToAccountInfoMapper::map);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteByUserKey(String userKey) {

        accountRepository.deleteByUserKey(userKey);
    }
}
