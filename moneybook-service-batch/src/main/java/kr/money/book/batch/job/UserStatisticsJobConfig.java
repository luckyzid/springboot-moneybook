package kr.money.book.batch.job;

import kr.money.book.batch.entity.UserDto;
import kr.money.book.batch.entity.UserStatistics;
import kr.money.book.batch.listener.UserStatisticsJobCompletionListener;
import kr.money.book.batch.step.UserStatisticsProcessor;
import kr.money.book.batch.step.UserStatisticsReader;
import kr.money.book.batch.step.UserStatisticsWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class UserStatisticsJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserStatisticsJobCompletionListener completionListener;
    private final UserStatisticsReader userStatisticsReader;
    private final UserStatisticsProcessor userStatisticsProcessor;
    private final UserStatisticsWriter userStatisticsWriter;

    @Bean
    public Job userStatisticsJob() {
        return new JobBuilder("userStatisticsJob", jobRepository)
            .listener(completionListener)
            .start(userStatisticsStep())
            .build();
    }

    @Bean
    public Step userStatisticsStep() {
        return new StepBuilder("userStatisticsStep", jobRepository)
            .<UserDto, UserStatistics>chunk(100, transactionManager)
            .reader(userStatisticsReader)
            .processor(userStatisticsProcessor)
            .writer(userStatisticsWriter)
            .build();
    }
} 