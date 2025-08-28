package kr.money.book.batch.step;

import java.math.BigDecimal;
import java.time.LocalDate;
import kr.money.book.batch.entity.UserDto;
import kr.money.book.batch.entity.UserStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class UserStatisticsProcessor implements ItemProcessor<UserDto, UserStatistics> {

    @Value("#{jobParameters['statisticsDate']}")
    private String statisticsDateString;

    @Override
    public UserStatistics process(UserDto user) {
        LocalDate statisticsDate = LocalDate.now();
        if (statisticsDateString != null && !statisticsDateString.isEmpty()) {
            statisticsDate = LocalDate.parse(statisticsDateString);
        }

        log.info("Processing user statistics for user: {}, date: {}", user.getUserKey(), statisticsDate);
        
        // 여기서 실제로는 해당 사용자의 계정 내역을 조회하여 수입/지출 합계를 계산해야 합니다.
        // 간단한 예제로 임의의 값을 설정합니다.
        BigDecimal totalIncome = BigDecimal.valueOf(Math.random() * 1000000);
        BigDecimal totalExpense = BigDecimal.valueOf(Math.random() * 500000);
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        return UserStatistics.builder()
            .userKey(user.getUserKey())
            .statisticsDate(statisticsDate)
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .balance(balance)
            .processStatus("PROCESSED")
            .build();
    }
}