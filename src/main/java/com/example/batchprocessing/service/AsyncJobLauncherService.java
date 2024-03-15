package com.example.batchprocessing.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncJobLauncherService {

    @Autowired
    protected JobLauncher jobLauncher;

    @Autowired
    protected JobLocator jobLocator;

    @Async("taskExecutor")
    public void launchJobAsync(String jobName, String jobIdentifier) throws Exception {
        Job job = jobLocator.getJob(jobName);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("uniqueId", jobIdentifier)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job, jobParameters);
    }
}
