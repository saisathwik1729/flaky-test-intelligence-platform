package com.ftip.ftip.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TestIdentityResponse {
    private UUID id;
    private String testName;
    private String testClass;
    private String ownerEmail;
    private String currentState;
    private double flakinessScore;
    private LocalDateTime lastEvaluationAt;
    private LocalDateTime createdAt;
    private String teamName;
}
