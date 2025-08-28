package kr.money.book.scheduler.repository;

import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.scheduler.entity.JobExecutionLog;
import kr.money.book.scheduler.entity.ScheduleJob.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, Long> {

    List<JobExecutionLog> findByJobName(String jobName);
    
    List<JobExecutionLog> findByJobNameAndStatus(String jobName, JobStatus status);
    
    List<JobExecutionLog> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    Page<JobExecutionLog> findByJobNameOrderByStartTimeDesc(String jobName, Pageable pageable);
} 