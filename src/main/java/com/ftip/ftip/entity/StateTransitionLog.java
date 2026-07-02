package com.ftip.ftip.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="state_transition_log")
@Data

public class StateTransitionLog {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="test_identity_id",nullable=false)
    private TestIdentity testIdentity;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_state", columnDefinition = "varchar(50)")
    private TestState fromState;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_state", columnDefinition = "varchar(50)")
    private TestState toState;

    private String reason;

    @Column(name="triggered_by")
    private String triggeredBy;

    @Column(name="score_at_transition")
    private double scoreAtTransition;

    @Column(name="transitioned_at")
    private LocalDateTime transitionedAt;

    @PrePersist
    public void prePersist()
    {
        this.transitionedAt = LocalDateTime.now();
    }
}
