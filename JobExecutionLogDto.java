package com.airtribe.jobscheduler.dto;
import com.airtribe.jobscheduler.entity.JobExecutionLog;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Builder
public class JobExecutionLogDto {
    private Long id;
    private String jobName;
    private String jobGroup;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private JobExecutionLog.JobStatus status;
    private String resultMessage;
}
