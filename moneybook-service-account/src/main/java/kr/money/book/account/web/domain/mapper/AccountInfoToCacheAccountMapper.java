package kr.money.book.account.web.domain.mapper;

import kr.money.book.account.web.domain.valueobject.AccountInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.common.valueobject.CacheAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountInfoToCacheAccountMapper implements DomainMapper<AccountInfo, CacheAccount> {

    @Override
    public CacheAccount map(AccountInfo accountInfo) {

        return CacheAccount.builder()
            .idx(accountInfo.idx())
            .name(accountInfo.name())
            .type(accountInfo.type().name())
            .build();
    }
}
