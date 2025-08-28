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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_schedule_job",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_mb_schedule_job_unique", columnNames = {"job_name"})
    },
    indexes = {
        @Index(name = "idx_mb_schedule_job_status", columnList = "status"),
        @Index(name = "idx_mb_schedule_job_update_date", columnList = "update_date")
    }
)
public class ScheduleJob extends MutableBaseEntity {

    public enum JobStatus {
        WAITING, RUNNING, COMPLETED, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "cron_expression", length = 50)
    private String cronExpression;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status;

    @Column(name = "last_execution_time")
    private LocalDateTime lastExecutionTime;

    @Column(name = "next_execution_time")
    private LocalDateTime nextExecutionTime;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    public ScheduleJob(String jobName, String jobDescription, String cronExpression, 
                     String className, String methodName, JobStatus status, 
                     LocalDateTime lastExecutionTime, LocalDateTime nextExecutionTime,
                     String errorMessage) {
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.cronExpression = cronExpression;
        this.className = className;
        this.methodName = methodName;
        this.status = status != null ? status : JobStatus.WAITING;
        this.lastExecutionTime = lastExecutionTime;
        this.nextExecutionTime = nextExecutionTime;
        this.errorMessage = errorMessage;
    }

    public void updateStatus(JobStatus status) {
        this.status = status;
    }

    public void updateLastExecutionTime(LocalDateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public void updateNextExecutionTime(LocalDateTime nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public void updateErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void updateCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
} 