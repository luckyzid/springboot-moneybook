package kr.money.book.account.web.domain.mapper;

import kr.money.book.account.web.domain.entity.Account;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountToAccountInfoMapper implements DomainMapper<Account, AccountInfo> {

    @Override
    public AccountInfo map(Account account) {

        if (account == null) {
            return null;
        }

        return AccountInfo.builder()
            .idx(account.getIdx())
            .userKey(account.getUserKey())
            .name(account.getName())
            .type(account.getType())
            .build();
    }
}
