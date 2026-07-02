package com.ftip.ftip.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data

public class WebhookPayloadRequest {
    @NotNull(message="Team Id is requires")
    private UUID teamId;
    private String branch;
    private String commitSha;
    private String environment;

    @Valid
    @NotNull(message = "Results list cannot be null")
    private List<TestResultRequest>results;
}
