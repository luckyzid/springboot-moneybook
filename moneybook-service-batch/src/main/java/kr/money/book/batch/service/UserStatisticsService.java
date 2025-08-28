package kr.money.book.batch.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.money.book.batch.entity.UserStatistics;
import kr.money.book.batch.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatisticsService {

    private final UserStatisticsRepository userStatisticsRepository;
    
    @Transactional(readOnly = true)
    public List<UserStatistics> findAllUserStatistics() {
        return userStatisticsRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<UserStatistics> findByUserKeyAndDate(String userKey, LocalDate date) {
        return userStatisticsRepository.findByUserKeyAndStatisticsDate(userKey, date);
    }
    
    @Transactional(readOnly = true)
    public List<UserStatistics> findByProcessStatus(String processStatus) {
        return userStatisticsRepository.findByProcessStatus(processStatus);
    }
    
    @Transactional
    public UserStatistics createOrUpdateUserStatistics(String userKey, LocalDate statisticsDate, 
                                             BigDecimal totalIncome, BigDecimal totalExpense) {
        
        Optional<UserStatistics> existingStats = userStatisticsRepository
            .findByUserKeyAndStatisticsDate(userKey, statisticsDate);
        
        if (existingStats.isPresent()) {
            UserStatistics stats = existingStats.get();
            UserStatistics updatedStats = UserStatistics.builder()
                .userKey(userKey)
                .statisticsDate(statisticsDate)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .processStatus("COMPLETED")
                .build();
            
            return userStatisticsRepository.save(updatedStats);
        } else {
            UserStatistics newStats = UserStatistics.builder()
                .userKey(userKey)
                .statisticsDate(statisticsDate)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .processStatus("COMPLETED")
                .build();
            
            return userStatisticsRepository.save(newStats);
        }
    }
    
    @Transactional
    public void updateProcessStatus(Long id, String processStatus) {
        userStatisticsRepository.findById(id).ifPresent(stats -> {
            stats.updateProcessStatus(processStatus);
            userStatisticsRepository.save(stats);
        });
    }
} 