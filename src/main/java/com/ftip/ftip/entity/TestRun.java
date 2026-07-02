package com.ftip.ftip.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="test_run")
@Data

public class TestRun {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="test_identity_id", nullable=false)
    private TestIdentity testIdentity;

    @Column(nullable=false)
    private String result;

    @Column(name="duration_ms")
    private long durationMs;

    private String branch;

    @Column(name="commit_sha")
    private String commitSha;

    private String environment;

    @Column(name="run_at")
    private LocalDateTime runAt;

    @PrePersist
    public void prePersist(){
        this.runAt=LocalDateTime.now();
    }

}
