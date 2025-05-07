package kr.money.book.user.web.domain.repository;

import java.util.Optional;
import kr.money.book.user.web.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdx(@Param("idx") Long idx);

    Optional<User> findByProviderAndEmail(String provider, String email);

    Optional<User> findByUserKey(@Param("userKey") String userKey);

//	@EntityGraph(attributePaths = {"tokens"})
//	Optional<User> findByUserKeyWithTokens(@Param("userKey") String userKey);

    Optional<User> findByEmail(@Param("email") String email);

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);

    @Transactional
    @Modifying
    void deleteByProviderAndEmail(@Param("provider") String provider, @Param("email") String email);

    @Transactional
    @Modifying
    void deleteByEmail(@Param("email") String email);
}
