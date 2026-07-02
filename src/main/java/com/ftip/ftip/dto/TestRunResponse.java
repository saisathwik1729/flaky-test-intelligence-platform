package com.ftip.ftip.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TestRunResponse {
    private UUID id;
    private String result;
    private long durationMs;
    private String branch;
    private String commitSha;
    private String environment;
    private LocalDateTime runAt;
}
