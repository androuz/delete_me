package com.example.batchprocessing.controller;

import com.example.batchprocessing.service.AsyncJobLauncherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class BatchJobLaunchController {

    @Autowired
    private AsyncJobLauncherService asyncJobLauncherService;

    @GetMapping("/launch/importJsonJob")
    public String launchImportJsonJob() throws Exception {
        // Generate a unique job identifier
        String jobIdentifier = UUID.randomUUID().toString();

        // Launch the job asynchronously with the unique identifier
        asyncJobLauncherService.launchJobAsync("importJsonJob", jobIdentifier);

        // Return the unique identifier immediately
        return jobIdentifier;
    }
}
