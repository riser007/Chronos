package com.airtribe.jobscheduler.controller;

import com.airtribe.jobscheduler.dto.ApiResponse;
import com.airtribe.jobscheduler.dto.JobRequest;
import com.airtribe.jobscheduler.dto.JobResponse;
import com.airtribe.jobscheduler.entity.JobExecutionLog;
import com.example.jobscheduler.repository.JobExecutionLogRepository;
import com.airtribe.jobscheduler.service.JobSchedulingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobSchedulingService jobSchedulingService;
    private final JobExecutionLogRepository logRepository;

    public JobController(JobSchedulingService jobSchedulingService, JobExecutionLogRepository logRepository) {
        this.jobSchedulingService = jobSchedulingService;
        this.logRepository = logRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> scheduleJob(@Valid @RequestBody JobRequest jobRequest) {
        jobSchedulingService.scheduleJob(jobRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Job scheduled successfully."));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobSchedulingService.getAllJobs());
    }

    @GetMapping("/{group}/{name}")
    public ResponseEntity<JobResponse> getJobDetails(@PathVariable String group, @PathVariable String name) {
        return ResponseEntity.ok(jobSchedulingService.getJob(group, name));
    }

    @DeleteMapping("/{group}/{name}")
    public ResponseEntity<ApiResponse> cancelJob(@PathVariable String group, @PathVariable String name) {
        jobSchedulingService.cancelJob(group, name);
        return ResponseEntity.ok(new ApiResponse(true, "Job canceled successfully."));
    }

    @PostMapping("/{group}/{name}/pause")
    public ResponseEntity<ApiResponse> pauseJob(@PathVariable String group, @PathVariable String name) {
        jobSchedulingService.pauseJob(group, name);
        return ResponseEntity.ok(new ApiResponse(true, "Job paused successfully."));
    }

    @PostMapping("/{group}/{name}/resume")
    public ResponseEntity<ApiResponse> resumeJob(@PathVariable String group, @PathVariable String name) {
        jobSchedulingService.resumeJob(group, name);
        return ResponseEntity.ok(new ApiResponse(true, "Job resumed successfully."));
    }

    @PutMapping("/{group}/{name}/reschedule")
    public ResponseEntity<ApiResponse> rescheduleJob(@PathVariable String group, @PathVariable String name, @RequestBody String cronExpression) {
        // The cron expression is expected as a raw string in the body
        jobSchedulingService.rescheduleJob(group, name, cronExpression);
        return ResponseEntity.ok(new ApiResponse(true, "Job rescheduled successfully."));
    }

    @GetMapping("/{group}/{name}/history")
    public ResponseEntity<List<JobExecutionLog>> getJobHistory(@PathVariable String group, @PathVariable String name) {
        return ResponseEntity.ok(logRepository.findByJobGroupAndJobNameOrderByStartTimeDesc(group, name));
    }
}