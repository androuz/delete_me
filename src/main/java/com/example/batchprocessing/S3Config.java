package com.example.batchprocessing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;

@Configuration
public class S3Config {
    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${cloud.aws.credentials.accessKey}")
    private String awsAccessKeyId ;

    @Value("${cloud.aws.credentials.secretKey}")
    private String awsSecretAccessKey ;

    @Value("${cloud.aws.storage.url}")
    private String customEndpointUrl  ;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);
        System.out.println("Configured AWS Region: " + URI.create(customEndpointUrl)); // This should now print the correct value

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .endpointOverride(URI.create(customEndpointUrl))
                .region(Region.of(awsRegion))
                .build();
    }
}