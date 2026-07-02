package com.ftip.ftip.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="daily_metrics",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team_id", "date"})
})
@Data


public class DailyMetrics {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="team_id",nullable=false)
    private Team team;

    private LocalDate date;

    @Column(name="total_runs")
    private int totalRuns=0;

    @Column(name="total_failures")
    private int totalFailures=0;

    @Column(name="flaky_test_count")
    private int flakyTestCount=0;

    @Column(name="quarantined_count")
    private int quarantinedCount=0;

    @Column(name="ci_minutes_wasted")
    private double ciMinutesWasted=0.0;
}
