package kr.money.book.batch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.money.book.batch.entity.UserStatistics;
import kr.money.book.batch.repository.UserStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserStatisticsServiceTest {

    @Mock
    private UserStatisticsRepository userStatisticsRepository;

    @InjectMocks
    private UserStatisticsService userStatisticsService;

    @Test
    @DisplayName("사용자 통계 생성 테스트")
    void createUserStatisticsTest() {
        // given
        String userKey = "test-user-1";
        LocalDate date = LocalDate.now();
        BigDecimal income = BigDecimal.valueOf(1000000);
        BigDecimal expense = BigDecimal.valueOf(500000);
        BigDecimal balance = income.subtract(expense);
        
        UserStatistics stats = UserStatistics.builder()
            .userKey(userKey)
            .statisticsDate(date)
            .totalIncome(income)
            .totalExpense(expense)
            .balance(balance)
            .processStatus("COMPLETED")
            .build();
        
        Mockito.when(userStatisticsRepository.findByUserKeyAndStatisticsDate(userKey, date))
            .thenReturn(Optional.empty());
        Mockito.when(userStatisticsRepository.save(Mockito.any(UserStatistics.class)))
            .thenReturn(stats);

        // when
        UserStatistics result = userStatisticsService.createOrUpdateUserStatistics(
            userKey, date, income, expense);

        // then
        assertNotNull(result);
        assertEquals(userKey, result.getUserKey());
        assertEquals(date, result.getStatisticsDate());
        assertEquals(income, result.getTotalIncome());
        assertEquals(expense, result.getTotalExpense());
        assertEquals(balance, result.getBalance());
        assertEquals("COMPLETED", result.getProcessStatus());
    }

    @Test
    @DisplayName("사용자 통계 조회 테스트")
    void findUserStatisticsTest() {
        // given
        String userKey = "test-user-2";
        LocalDate date = LocalDate.now();
        
        UserStatistics stats = UserStatistics.builder()
            .userKey(userKey)
            .statisticsDate(date)
            .totalIncome(BigDecimal.valueOf(2000000))
            .totalExpense(BigDecimal.valueOf(1000000))
            .balance(BigDecimal.valueOf(1000000))
            .processStatus("COMPLETED")
            .build();
        
        Mockito.when(userStatisticsRepository.findByUserKeyAndStatisticsDate(userKey, date))
            .thenReturn(Optional.of(stats));

        // when
        Optional<UserStatistics> result = userStatisticsService.findByUserKeyAndDate(userKey, date);

        // then
        assertTrue(result.isPresent());
        assertEquals(userKey, result.get().getUserKey());
    }

    @Test
    @DisplayName("프로세스 상태로 사용자 통계 찾기 테스트")
    void findByProcessStatusTest() {
        // given
        String processStatus = "PENDING";
        List<UserStatistics> statsList = List.of(
            UserStatistics.builder()
                .userKey("test-user-4")
                .statisticsDate(LocalDate.now())
                .totalIncome(BigDecimal.valueOf(4000000))
                .totalExpense(BigDecimal.valueOf(2000000))
                .balance(BigDecimal.valueOf(2000000))
                .processStatus(processStatus)
                .build()
        );
        
        Mockito.when(userStatisticsRepository.findByProcessStatus(processStatus))
            .thenReturn(statsList);

        // when
        List<UserStatistics> result = userStatisticsService.findByProcessStatus(processStatus);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(processStatus, result.get(0).getProcessStatus());
    }
} 