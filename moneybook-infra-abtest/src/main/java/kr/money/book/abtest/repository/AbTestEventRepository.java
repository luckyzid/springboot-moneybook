package kr.money.book.abtest.repository;

import java.util.Optional;
import kr.money.book.abtest.entity.AbTestEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbTestEventRepository extends JpaRepository<AbTestEvent, Long> {

    Optional<AbTestEvent> findTopByExperimentKeyAndUserIdOrderByAssignedAtDesc(String experimentKey, Long userId);
}
