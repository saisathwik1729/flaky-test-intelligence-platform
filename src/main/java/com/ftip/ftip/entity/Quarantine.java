package com.ftip.ftip.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="quarantine")
@Data


public class Quarantine {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="test_identity_id",nullable=false)
    private TestIdentity testIdentity;

    @Column(name="quarantined_at")
    private LocalDateTime quarantinedAt;

    @Column(name="quarantined_by")
    private String quarantinedBy;

    @Column(name="consecutive_passes")
    private int consecutivePasses=0;

    @Column(name="recovery_started_at")
    private LocalDateTime recoveryStartedAt;

    @Column(name="recovered_at")
    private LocalDateTime recoveredAt;

}
