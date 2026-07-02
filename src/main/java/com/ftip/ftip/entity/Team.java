package com.ftip.ftip.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="team")
@Data

public class Team {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable=false)
    private String name;

    @Column(name="repo_url")
    private String repoUrl;

    @Column(name="flakiness_threshold")
    private int flakinessThreshold=60;

    @Column(name="auto_quarantine_threshold")
    private int autoQuarantineThreshold=85;

    @Column(name="recovery_streak_required")
    private int recoveryStreakRequired=10;

    @Column(name="scoring_window_days")
    private int scoringWindowDays=30;

    @Column(name="created_at")
    private LocalDate createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDate.now();
    }
}
