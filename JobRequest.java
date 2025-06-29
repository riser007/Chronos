package com.airtribe.jobscheduler.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Map;
@Data
public class JobRequest {
    @NotBlank
    private String jobName;
    @NotBlank
    private String jobGroup;
    private String cronExpression; // Null for one-time jobs
    private String jobType; // e.g., "EMAIL", "DATA_PROCESSING"
    private Map<String, Object> data; // Custom data for the job
}