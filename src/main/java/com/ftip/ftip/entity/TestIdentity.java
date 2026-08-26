package com.ftip.ftip.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Table(name="test_identity")
@Data

public class TestIdentity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;
    
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
