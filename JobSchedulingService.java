package com.airtribe.jobscheduler.service;

import com.airtribe.jobscheduler.dto.JobRequest;
import com.airtribe.jobscheduler.dto.JobResponse;
import com.airtribe.jobscheduler.exception.ResourceNotFoundException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSchedulingService {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ApplicationContext context;

    public void scheduleJob(JobRequest jobRequest) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = buildJobDetail(jobRequest);
            Trigger trigger = buildTrigger(jobRequest, jobDetail);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new IllegalStateException("Error scheduling job: " + e.getMessage(), e);
        }
    }

    public List<JobResponse> getAllJobs() {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            return scheduler.getJobKeys(null).stream()
                    .map(jobKey -> {
                        try {
                            return buildJobResponse(scheduler, jobKey);
                        } catch (SchedulerException e) {
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            throw new IllegalStateException("Error fetching jobs: " + e.getMessage(), e);
        }
    }

    public JobResponse getJob(String jobGroup, String jobName) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = new JobKey(jobName, jobGroup);
            if(!scheduler.checkExists(jobKey)) {
                throw new ResourceNotFoundException("Job not found with name " + jobName + " and group " + jobGroup);
            }
            return buildJobResponse(scheduler, jobKey);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Error fetching job details: " + e.getMessage(), e);
        }
    }

    public void cancelJob(String jobGroup, String jobName) {
        try {
            schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Error cancelling job: " + e.getMessage(), e);
        }
    }

    public void pauseJob(String jobGroup, String jobName) {
        try {
            schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Error pausing job: " + e.getMessage(), e);
        }
    }

    public void resumeJob(String jobGroup, String jobName) {
        try {
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Error resuming job: " + e.getMessage(), e);
        }
    }

    public void rescheduleJob(String jobGroup, String jobName, String cronExpression) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = new TriggerKey(jobName, jobGroup);
            Trigger oldTrigger = scheduler.getTrigger(triggerKey);

            if (oldTrigger == null) {
                throw new ResourceNotFoundException("Job with the specified trigger not found.");
            }

            TriggerBuilder<Trigger> triggerBuilder = oldTrigger.getTriggerBuilder();
            Trigger newTrigger = triggerBuilder
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Error rescheduling job: " + e.getMessage(), e);
        }
    }

    private JobDetail buildJobDetail(JobRequest jobRequest) {
        JobDataMap jobDataMap = new JobDataMap(jobRequest.getData());
        Class<? extends Job> jobClass = getJobClass(jobRequest.getJobType());

        return JobBuilder.newJob(jobClass)
                .withIdentity(jobRequest.getJobName(), jobRequest.getJobGroup())
                .withDescription("Job created via API")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobRequest jobRequest, JobDetail jobDetail) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup())
                .withDescription("API Trigger");

        if (jobRequest.getCronExpression() != null && !jobRequest.getCronExpression().isBlank()) {
            builder.withSchedule(CronScheduleBuilder.cronSchedule(jobRequest.getCronExpression()));
        } else {
            builder.startNow();
        }

        return builder.build();
    }

    private JobResponse buildJobResponse(Scheduler scheduler, JobKey jobKey) throws SchedulerException {
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        Trigger trigger = triggers.isEmpty() ? null : triggers.get(0);
        Date nextFireTime = trigger != null ? trigger.getNextFireTime() : null;
        Date lastFiredTime = trigger != null ? trigger.getPreviousFireTime() : null;
        Trigger.TriggerState triggerState = trigger != null ? scheduler.getTriggerState(trigger.getKey()) : Trigger.TriggerState.NONE;

        return JobResponse.builder()
                .jobName(jobKey.getName())
                .jobGroup(jobKey.getGroup())
                .jobStatus(triggerState.name())
                .scheduleTime(trigger != null ? trigger.getStartTime().toString() : "N/A")
                .lastFiredTime(lastFiredTime != null ? lastFiredTime.toString() : "N/A")
                .nextFireTime(nextFireTime != null ? nextFireTime.toString() : "N/A")
                .build();
    }

    private Class<? extends Job> getJobClass(String jobType) {
        try {
            // This maps a string from the API to an actual Job class.
            // Be careful with this in production; ensure only allowed job types can be created.
            return (Class<? extends Job>) Class.forName("com.example.jobscheduler.jobs." + jobType + "Job");
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid job type: " + jobType);
        }
    }
}