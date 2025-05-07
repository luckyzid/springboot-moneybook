package kr.money.book.user.web.domain.repository;

import java.util.List;
import java.util.Optional;
import kr.money.book.user.web.domain.entity.User;
import kr.money.book.user.web.domain.entity.UserToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<User> findByIdx(@Param("idx") Long idx);

    Optional<UserToken> findByToken(@Param("token") String token);

    @EntityGraph(attributePaths = {"user"})
    List<UserToken> findByUser_UserKeyAndIsActive(
        @Param("userKey") String userKey,
        @Param("isActive") int isActive
    );

    Optional<UserToken> findTopByUser_UserKeyAndIsActiveOrderByUpdateDateDesc(String userKey, int isActive);

    @Transactional
    @Modifying
    @Query(" UPDATE UserToken ut SET ut.isActive = 0 WHERE ut.user.userKey = :userKey ")
    void deactivateTokensByUserKey(@Param("userKey") String userKey);

    @Transactional
    @Modifying
    void deleteByUser_UserKey(@Param("userKey") String userKey);
}
