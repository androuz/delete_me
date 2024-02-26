package com.example.batchprocessing.controller;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Long launchImportJsonJob() throws Exception {
        Job job = jobLocator.getJob("importJsonJob");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        return jobExecution.getId();
    }
}