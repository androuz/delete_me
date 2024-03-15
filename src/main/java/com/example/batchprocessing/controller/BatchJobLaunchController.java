package com.example.batchprocessing.controller;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class BatchJobLaunchController {

    @Autowired
    protected JobLauncher jobLauncher;

    @Autowired
    protected JobLocator jobLocator;

    @Autowired
    public BatchJobLaunchController(JobLauncher jobLauncher, JobLocator jobLocator) {
    }


    @GetMapping("/launch/importJsonJob")
    public String launchImportJsonJob() throws Exception {
        // Generate a unique job identifier
        String jobIdentifier = UUID.randomUUID().toString();

        // Launch the job asynchronously with the unique identifier
        launchJobAsync("importJsonJob", jobIdentifier);

        // Return the unique identifier immediately
        return jobIdentifier;
    }

    @Async("taskExecutor")
    public void launchJobAsync(String jobName, String jobIdentifier) throws Exception {
        Job job = jobLocator.getJob(jobName);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("uniqueId", jobIdentifier)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // This launches the job asynchronously; no need to capture the result here
        jobLauncher.run(job, jobParameters);
    }
}
