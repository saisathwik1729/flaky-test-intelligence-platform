package com.ftip.ftip.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeamRequest {
    @NotBlank(message="Team name is required")
    private String name;
    private String repoUrl;
    private int flakinessThreshold=60;
    private int autoQuarantineThreshold=85;
    private int recoveryStreakRequired=10;
    private int scoringWindowDays=30;
}
