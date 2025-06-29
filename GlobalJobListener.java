package com.airtribe.jobscheduler.listener;

import com.airtribe.jobscheduler.entity.JobExecutionLog;
import com.example.jobscheduler.repository.JobExecutionLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class GlobalJobListener implements JobListener {

    private final JobExecutionLogRepository logRepository;
    private static final int MAX_RETRIES = 3;

    public GlobalJobListener(JobExecutionLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public String getName() {
        return "GlobalJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        log.info("Job {} is about to be executed.", jobKey);

        JobExecutionLog jobLog = new JobExecutionLog();
        jobLog.setJobName(jobKey.getName());
        jobLog.setJobGroup(jobKey.getGroup());
        jobLog.setStartTime(LocalDateTime.now());
        jobLog.setStatus(JobExecutionLog.JobStatus.RUNNING);

        // Store the log ID in the job data map to update it later
        JobExecutionLog savedLog = logRepository.save(jobLog);
        context.getJobDetail().getJobDataMap().put("logId", savedLog.getId());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.warn("Execution of job {} was vetoed.", context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobKey jobKey = context.getJobDetail().getKey();
        Long logId = context.getJobDetail().getJobDataMap().getLong("logId");
        JobExecutionLog jobLog = logRepository.findById(logId).orElse(new JobExecutionLog());

        jobLog.setEndTime(LocalDateTime.now());

        if (jobException != null) {
            log.error("Exception thrown by job {}", jobKey, jobException);
            int retries = context.getRefireCount();

            jobLog.setResultMessage("Error: " + jobException.getMessage());

            if (retries < MAX_RETRIES) {
                jobLog.setStatus(JobExecutionLog.JobStatus.RETRYING);
                log.info("Job {} failed. Retrying... (Attempt {}/{})", jobKey, retries + 1, MAX_RETRIES);
                // Set up for retry
                try {
                    jobException.setRefireImmediately(true);
                } catch (Exception e) {
                    log.error("Could not set job for refire", e);
                }
            } else {
                jobLog.setStatus(JobExecutionLog.JobStatus.PERMANENTLY_FAILED);
                log.error("Job {} failed after {} retries. Marking as permanently failed.", jobKey, MAX_RETRIES);
            }
        } else {
            log.info("Job {} successfully executed.", jobKey);
            jobLog.setStatus(JobExecutionLog.JobStatus.SUCCESS);
            jobLog.setResultMessage("Completed successfully.");
        }
        logRepository.save(jobLog);
    }
}