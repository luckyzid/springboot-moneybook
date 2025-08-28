package kr.money.book.scheduler.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.money.book.scheduler.entity.JobExecutionLog;
import kr.money.book.scheduler.entity.ScheduleJob;
import kr.money.book.scheduler.entity.ScheduleJob.JobStatus;
import kr.money.book.scheduler.repository.JobExecutionLogRepository;
import kr.money.book.scheduler.repository.ScheduleJobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ScheduleJobServiceTest {

    @Mock
    private ScheduleJobRepository scheduleJobRepository;

    @Mock
    private JobExecutionLogRepository jobExecutionLogRepository;

    @InjectMocks
    private ScheduleJobService scheduleJobService;

    @Test
    @DisplayName("스케줄 작업 등록 테스트")
    void registerJobTest() {
        // given
        String jobName = "testJob";
        String jobDescription = "Test Job Description";
        String cronExpression = "0 0 1 * * ?";
        String className = "kr.money.book.scheduler.task.TestTask";
        String methodName = "runTask";
        
        ScheduleJob job = ScheduleJob.builder()
            .jobName(jobName)
            .jobDescription(jobDescription)
            .cronExpression(cronExpression)
            .className(className)
            .methodName(methodName)
            .status(JobStatus.WAITING)
            .build();
        
        when(scheduleJobRepository.save(any(ScheduleJob.class))).thenReturn(job);

        // when
        ScheduleJob result = scheduleJobService.registerJob(
            jobName, jobDescription, cronExpression, className, methodName);

        // then
        assertNotNull(result);
        assertEquals(jobName, result.getJobName());
        assertEquals(jobDescription, result.getJobDescription());
        assertEquals(cronExpression, result.getCronExpression());
        assertEquals(className, result.getClassName());
        assertEquals(methodName, result.getMethodName());
        assertEquals(JobStatus.WAITING, result.getStatus());
    }

    @Test
    @DisplayName("작업 상태 업데이트 테스트")
    void updateJobStatusTest() {
        // given
        String jobName = "updateStatusJob";
        ScheduleJob job = ScheduleJob.builder()
            .jobName(jobName)
            .jobDescription("Update Status Job")
            .cronExpression("0 0 2 * * ?")
            .className("kr.money.book.scheduler.task.UpdateTask")
            .methodName("runTask")
            .status(JobStatus.WAITING)
            .build();
        
        ScheduleJob updatedJob = ScheduleJob.builder()
            .jobName(jobName)
            .jobDescription("Update Status Job")
            .cronExpression("0 0 2 * * ?")
            .className("kr.money.book.scheduler.task.UpdateTask")
            .methodName("runTask")
            .status(JobStatus.RUNNING)
            .build();
        
        when(scheduleJobRepository.findByJobName(jobName)).thenReturn(Optional.of(job));
        when(scheduleJobRepository.save(any(ScheduleJob.class))).thenReturn(updatedJob);

        // when
        ScheduleJob result = scheduleJobService.updateJobStatus(jobName, JobStatus.RUNNING);

        // then
        assertEquals(JobStatus.RUNNING, result.getStatus());
    }

    @Test
    @DisplayName("작업 실행 로그 시작 테스트")
    void startJobExecutionTest() {
        // given
        String jobName = "startLogJob";
        LocalDateTime now = LocalDateTime.now();
        
        ScheduleJob job = ScheduleJob.builder()
            .jobName(jobName)
            .jobDescription("Start Log Job")
            .cronExpression("0 0 3 * * ?")
            .className("kr.money.book.scheduler.task.StartLogTask")
            .methodName("runTask")
            .status(JobStatus.WAITING)
            .build();
        
        JobExecutionLog log = JobExecutionLog.builder()
            .jobName(jobName)
            .status(JobStatus.RUNNING)
            .startTime(now)
            .build();
        
        when(scheduleJobRepository.findByJobName(jobName)).thenReturn(Optional.of(job));
        when(scheduleJobRepository.save(any(ScheduleJob.class))).thenReturn(job);
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(log);

        // when
        JobExecutionLog result = scheduleJobService.startJobExecution(jobName);

        // then
        assertNotNull(result);
        assertEquals(jobName, result.getJobName());
        assertEquals(JobStatus.RUNNING, result.getStatus());
        assertNotNull(result.getStartTime());
    }

    @Test
    @DisplayName("작업 실행 이력 조회 테스트")
    void getJobExecutionHistoryTest() {
        // given
        String jobName = "historyJob";
        Pageable pageable = PageRequest.of(0, 10);
        
        List<JobExecutionLog> logs = List.of(
            JobExecutionLog.builder()
                .jobName(jobName)
                .status(JobStatus.COMPLETED)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusDays(1).plusMinutes(5))
                .durationMs(300000L)
                .build(),
            JobExecutionLog.builder()
                .jobName(jobName)
                .status(JobStatus.COMPLETED)
                .startTime(LocalDateTime.now().minusHours(12))
                .endTime(LocalDateTime.now().minusHours(12).plusMinutes(5))
                .durationMs(300000L)
                .build()
        );
        
        Page<JobExecutionLog> page = new PageImpl<>(logs, pageable, logs.size());
        when(jobExecutionLogRepository.findByJobNameOrderByStartTimeDesc(jobName, pageable)).thenReturn(page);

        // when
        Page<JobExecutionLog> result = scheduleJobService.getJobExecutionHistory(jobName, pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
} 