package kr.money.book.scheduler.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.money.book.scheduler.entity.JobExecutionLog;
import kr.money.book.scheduler.entity.ScheduleJob;
import kr.money.book.scheduler.entity.ScheduleJob.JobStatus;
import kr.money.book.scheduler.repository.JobExecutionLogRepository;
import kr.money.book.scheduler.repository.ScheduleJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleJobService {

    private final ScheduleJobRepository scheduleJobRepository;
    private final JobExecutionLogRepository jobExecutionLogRepository;

    @Transactional(readOnly = true)
    public List<ScheduleJob> findAllJobs() {
        return scheduleJobRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ScheduleJob> findJobByName(String jobName) {
        return scheduleJobRepository.findByJobName(jobName);
    }

    @Transactional(readOnly = true)
    public List<ScheduleJob> findJobsByStatus(JobStatus status) {
        return scheduleJobRepository.findByStatus(status);
    }

    @Transactional
    public ScheduleJob registerJob(String jobName, String jobDescription, String cronExpression,
                             String className, String methodName) {

        ScheduleJob job = ScheduleJob.builder()
            .jobName(jobName)
            .jobDescription(jobDescription)
            .cronExpression(cronExpression)
            .className(className)
            .methodName(methodName)
            .status(JobStatus.WAITING)
            .build();

        return scheduleJobRepository.save(job);
    }

    @Transactional
    public ScheduleJob updateJobStatus(String jobName, JobStatus status) {
        Optional<ScheduleJob> jobOptional = scheduleJobRepository.findByJobName(jobName);
        if (jobOptional.isPresent()) {
            ScheduleJob job = jobOptional.get();
            job.updateStatus(status);
            return scheduleJobRepository.save(job);
        }
        throw new IllegalArgumentException("Job not found with name: " + jobName);
    }

    @Transactional
    public JobExecutionLog startJobExecution(String jobName) {
        JobExecutionLog log = JobExecutionLog.builder()
            .jobName(jobName)
            .status(JobStatus.RUNNING)
            .startTime(LocalDateTime.now())
            .build();
        
        // 작업 상태도 업데이트
        updateJobStatus(jobName, JobStatus.RUNNING);
        
        return jobExecutionLogRepository.save(log);
    }

    @Transactional
    public JobExecutionLog completeJobExecution(Long logId, LocalDateTime endTime) {
        JobExecutionLog log = jobExecutionLogRepository.findById(logId)
            .orElseThrow(() -> new IllegalArgumentException("Log not found with id: " + logId));
        
        long durationMs = endTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli() - 
                        log.getStartTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        
        log.complete(endTime, durationMs);
        
        // 작업 상태도 업데이트
        updateJobStatus(log.getJobName(), JobStatus.COMPLETED);
        
        return jobExecutionLogRepository.save(log);
    }

    @Transactional
    public JobExecutionLog failJobExecution(Long logId, LocalDateTime endTime, String errorMessage) {
        JobExecutionLog log = jobExecutionLogRepository.findById(logId)
            .orElseThrow(() -> new IllegalArgumentException("Log not found with id: " + logId));
        
        long durationMs = endTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli() - 
                        log.getStartTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        
        log.fail(endTime, durationMs, errorMessage);
        
        // 작업 상태도 업데이트
        updateJobStatus(log.getJobName(), JobStatus.FAILED);
        
        return jobExecutionLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<JobExecutionLog> getJobExecutionHistory(String jobName, Pageable pageable) {
        return jobExecutionLogRepository.findByJobNameOrderByStartTimeDesc(jobName, pageable);
    }
} 