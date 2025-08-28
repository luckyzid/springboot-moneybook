package kr.money.book.scheduler.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import kr.money.book.scheduler.entity.JobExecutionLog;
import kr.money.book.scheduler.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserStatisticsScheduleTask {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("userStatisticsJob")
    private Job userStatisticsJob;

    @Autowired
    private ScheduleJobService scheduleJobService;
    
    private static final String JOB_NAME = "userStatisticsJob";
    
    @Scheduled(cron = "${scheduler.user-statistics.cron:0 0 1 * * ?}") // 매일 새벽 1시 실행
    public void runUserStatisticsJob() {
        log.info("Starting scheduled user statistics job at: {}", LocalDateTime.now());
        
        JobExecutionLog executionLog = scheduleJobService.startJobExecution(JOB_NAME);
        
        try {
            // 어제 날짜를 기준으로 통계 데이터를 생성
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String dateString = yesterday.format(DateTimeFormatter.ISO_DATE);
            
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("statisticsDate", dateString)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
            
            org.springframework.batch.core.JobExecution jobExecution = 
                jobLauncher.run(userStatisticsJob, jobParameters);
            
            if (jobExecution.getStatus().isUnsuccessful()) {
                String errorMessage = jobExecution.getAllFailureExceptions().isEmpty() ? 
                    "Unknown error" : jobExecution.getAllFailureExceptions().get(0).getMessage();
                scheduleJobService.failJobExecution(executionLog.getIdx(), 
                    LocalDateTime.now(), errorMessage);
                
                log.error("User statistics job failed: {}", errorMessage);
            } else {
                scheduleJobService.completeJobExecution(executionLog.getIdx(), 
                    LocalDateTime.now());
                
                log.info("User statistics job completed successfully");
            }
            
        } catch (Exception e) {
            scheduleJobService.failJobExecution(executionLog.getIdx(), 
                LocalDateTime.now(), e.getMessage());
            
            log.error("Error running user statistics job", e);
        }
    }
} 