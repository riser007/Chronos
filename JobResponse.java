package com.airtribe.jobscheduler.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class JobResponse {
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String scheduleTime;
    private String lastFiredTime;
    private String nextFireTime;
}