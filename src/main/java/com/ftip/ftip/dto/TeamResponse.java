package com.ftip.ftip.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class TeamResponse {
    private UUID id;
    private String name;
    private String repoUrl;
    private int flakinessThreshold;
    private int autoQuarantineThreshold;
    private int recoveryStreakRequired;
    private int scoringWindowDays;
}
