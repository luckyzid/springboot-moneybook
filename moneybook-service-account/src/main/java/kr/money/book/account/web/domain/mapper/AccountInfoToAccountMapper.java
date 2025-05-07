package kr.money.book.account.web.domain.mapper;

import kr.money.book.account.web.domain.entity.Account;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountInfoToAccountMapper implements DomainMapper<AccountInfo, Account> {

    @Override
    public Account map(AccountInfo accountInfo) {

        return Account.builder()
            .userKey(accountInfo.userKey())
            .name(accountInfo.name())
            .type(accountInfo.type())
            .build();
    }
}
