package com.example.batchprocessing.controller;

import com.example.batchprocessing.service.AsyncJobLauncherService;
import com.example.batchprocessing.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class BatchJobLaunchController {

    @Autowired
    private AsyncJobLauncherService asyncJobLauncherService;

    @Autowired
    private S3Service s3Service;

    @GetMapping("/launch/importJsonJob")
    public String launchImportJsonJob() throws Exception {
        // Generate a unique job identifier
        String jobIdentifier = UUID.randomUUID().toString();

        // Launch the job asynchronously with the unique identifier
        asyncJobLauncherService.launchJobAsync("importJsonJob", jobIdentifier);

        // Return the unique identifier immediately
        return jobIdentifier;
    }

    @GetMapping("/presignedurl")
    public String getPresignedUrl() throws Exception {
        Duration urlExpiration = Duration.ofMinutes(60); // Example: URL valid for 10 minutes
        Map<String, String> metadata = Map.of();
        String presignedUrl = s3Service.createPresignedUrl("scib-des-ln-mercury", "bdh3bulkapitest/filename.json", metadata, urlExpiration);
        return presignedUrl;
    }
}
