package kr.money.book.scheduler.repository;

import java.util.List;
import java.util.Optional;
import kr.money.book.scheduler.entity.ScheduleJob;
import kr.money.book.scheduler.entity.ScheduleJob.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleJobRepository extends JpaRepository<ScheduleJob, Long> {

    Optional<ScheduleJob> findByJobName(String jobName);
    
    List<ScheduleJob> findByStatus(JobStatus status);
} 