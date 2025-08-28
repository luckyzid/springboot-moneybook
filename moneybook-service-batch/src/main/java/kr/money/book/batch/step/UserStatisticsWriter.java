package kr.money.book.batch.step;

import java.util.List;
import kr.money.book.batch.entity.UserStatistics;
import kr.money.book.batch.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class UserStatisticsWriter implements ItemWriter<UserStatistics> {

    private final UserStatisticsRepository userStatisticsRepository;

    @Override
    public void write(Chunk<? extends UserStatistics> chunk) {
        List<? extends UserStatistics> items = chunk.getItems();
        userStatisticsRepository.saveAll(items);
        
        log.info("Saved {} user statistics records", items.size());
    }
} 