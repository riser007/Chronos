package com.airtribe.jobscheduler.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataProcessingJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String sourceFile = dataMap.getString("sourceFile");

        log.info("Executing DataProcessingJob: Processing data from '{}'", sourceFile);
        try {
            // Simulate data processing
            Thread.sleep(5000); // Simulate long-running task
            log.info("DataProcessingJob completed successfully.");
        } catch (InterruptedException e) {
            log.error("Error while executing DataProcessingJob", e);
            throw new JobExecutionException(e);
        }
    }
}