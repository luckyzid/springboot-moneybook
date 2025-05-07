package kr.money.book.category.web.domain.repository;

import java.util.List;
import java.util.Optional;
import kr.money.book.category.web.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdx(@Param("idx") Long idx);

    List<Category> findByUserKey(@Param("userKey") String userKey);

    @Transactional
    @Modifying
    void deleteByUserKey(@Param("userKey") String userKey);
}
