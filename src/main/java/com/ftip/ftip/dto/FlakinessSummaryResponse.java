package com.ftip.ftip.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class FlakinessSummaryResponse {
    private UUID teamId;
    private String teamName;
    private int totalTests;
    private int healthyCount;
    private int suspectCount;
    private int flakyCount;
    private int quarantinedCount;
    private int recoveringCount;
    private double totalCiMinutesWasted;
}
