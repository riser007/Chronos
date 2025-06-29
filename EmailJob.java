package com.airtribe.jobscheduler.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String recipient = dataMap.getString("recipient");
        String subject = dataMap.getString("subject");

        log.info("Executing EmailJob: Sending email to {} with subject '{}'", recipient, subject);

        try {
            // Simulate sending email
            // In a real application, you would inject an EmailService here
            // e.g., @Autowired private EmailService emailService;
            // emailService.send(recipient, subject, "This is the job body.");

            // Simulate a potential failure for retry demonstration
            if ("fail@example.com".equals(recipient)) {
                throw new RuntimeException("Simulated email service failure.");
            }

            Thread.sleep(2000); // Simulate work
            log.info("EmailJob completed successfully.");
        } catch (Exception e) {
            log.error("Error while executing EmailJob", e);
            // Throw a JobExecutionException to trigger the listener's failure logic
            throw new JobExecutionException(e);
        }
    }
}