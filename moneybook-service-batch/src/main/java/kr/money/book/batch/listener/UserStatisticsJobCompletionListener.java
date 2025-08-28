package kr.money.book.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserStatisticsJobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("User statistics job started: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("User statistics job completed: {}", jobExecution.getJobInstance().getJobName());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("User statistics job failed: {}", jobExecution.getJobInstance().getJobName());
        }
    }
} 