package com.ftip.ftip.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="test_identity")
@Data

public class TestIdentity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="team_id",nullable=false)
    private Team team;

    @Column(name="test_name",nullable=false)
    private String testName;

    @Column(name="test_class")
    private String testClass;

    @Column(name="owner_email")
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", columnDefinition = "varchar(50)")
    private TestState currentState = TestState.HEALTHY;

    @Column(name="flakiness_score")
    private double flakinessScore=0.0;

    @Column(name="last_evaluated_at")
    private LocalDateTime lastEvaluatedAt;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist()
    {
        this.createdAt = LocalDateTime.now();
        if(this.currentState==null)
        {
            this.currentState = TestState.HEALTHY;
        }
    }
}
