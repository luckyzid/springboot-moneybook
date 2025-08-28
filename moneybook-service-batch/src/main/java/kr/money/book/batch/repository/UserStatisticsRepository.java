package kr.money.book.batch.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.money.book.batch.entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {

    Optional<UserStatistics> findByUserKeyAndStatisticsDate(String userKey, LocalDate statisticsDate);
    
    List<UserStatistics> findByStatisticsDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<UserStatistics> findByUserKey(String userKey);
    
    List<UserStatistics> findByUserKeyAndStatisticsDateBetween(String userKey, LocalDate startDate, LocalDate endDate);
    
    List<UserStatistics> findByProcessStatus(String processStatus);
} 