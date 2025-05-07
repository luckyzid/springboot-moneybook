package kr.money.book.account.web.domain.repository;

import java.util.List;
import java.util.Optional;
import kr.money.book.account.web.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByIdx(@Param("idx") Long idx);

    List<Account> findByUserKey(@Param("userKey") String userKey);

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
