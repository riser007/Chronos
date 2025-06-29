package com.airtribe.jobscheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job_execution_logs")
public class JobExecutionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobName;

    @Column(nullable = false)
    private String jobGroup;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(columnDefinition = "TEXT")
    private String resultMessage;

    public enum JobStatus {
        RUNNING,
        SUCCESS,
        FAILED,
        RETRYING,
        PERMANENTLY_FAILED
    }
}