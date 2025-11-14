package com.example.bedrock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.EqualJitterBackoffStrategy;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.time.Duration;

@Configuration
public class BedrockConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient() {
        // Configure retry policy with exponential backoff for 429 errors
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .numRetries(5) // Maximum number of retries
                .retryCondition(retryConditionContext -> {
                    // Retry on 429 (Too Many Requests) and other retryable errors
                    if (retryConditionContext.exception() != null) {
                        String errorMessage = retryConditionContext.exception().getMessage();
                        return errorMessage != null && (
                               errorMessage.contains("429") || 
                               errorMessage.contains("Too many requests") ||
                               errorMessage.contains("Throttling") ||
                               errorMessage.contains("ServiceException"));
                    }
                    return false;
                })
                .backoffStrategy(EqualJitterBackoffStrategy.builder()
                        .baseDelay(Duration.ofSeconds(1))
                        .maxBackoffTime(Duration.ofSeconds(30))
                        .build())
                .build();

        return BedrockRuntimeClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .overrideConfiguration(builder -> builder
                        .retryPolicy(retryPolicy)
                        .build())
                .build();
    }
}

