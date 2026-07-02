package com.ftip.ftip.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StateTransitionResponse {
    private UUID id;
    private String fromState;
    private String toState;
    private String reason;
    private String triggeredBy;
    private double scoreAtTransition;
    private LocalDateTime transitionedAt;
}
