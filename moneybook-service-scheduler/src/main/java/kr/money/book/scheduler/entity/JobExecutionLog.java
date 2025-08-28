package kr.money.book.scheduler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.money.book.rds.entity.MutableBaseEntity;
import kr.money.book.scheduler.entity.ScheduleJob.JobStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_job_execution_log",
    indexes = {
        @Index(name = "idx_mb_job_execution_log_job_name", columnList = "job_name"),
        @Index(name = "idx_mb_job_execution_log_status", columnList = "status"),
        @Index(name = "idx_mb_job_execution_log_start_time", columnList = "start_time"),
        @Index(name = "idx_mb_job_execution_log_update_date", columnList = "update_date")
    }
)
public class JobExecutionLog extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    public JobExecutionLog(String jobName, JobStatus status, LocalDateTime startTime, 
                          LocalDateTime endTime, Long durationMs, String errorMessage) {
        this.jobName = jobName;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMs = durationMs;
        this.errorMessage = errorMessage;
    }

    public void complete(LocalDateTime endTime, Long durationMs) {
        this.status = JobStatus.COMPLETED;
        this.endTime = endTime;
        this.durationMs = durationMs;
    }

    public void fail(LocalDateTime endTime, Long durationMs, String errorMessage) {
        this.status = JobStatus.FAILED;
        this.endTime = endTime;
        this.durationMs = durationMs;
        this.errorMessage = errorMessage;
    }
} 