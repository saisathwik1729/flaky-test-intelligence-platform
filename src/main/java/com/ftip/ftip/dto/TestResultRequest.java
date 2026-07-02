package com.ftip.ftip.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data

public class TestResultRequest {
    @NotBlank(message = "Test name connot be empty")
    private String testName;
    private String testClass;
    @NotBlank(message = "Result cannot be empty")
    private String result;
    @Positive(message = "Duration must be positive")
    private long durationMs;
    private String ownerEmail;
}
