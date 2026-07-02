package com.ftip.ftip.repository;
import com.ftip.ftip.entity.DailyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface DailyMetricsRepository extends JpaRepository<DailyMetrics, UUID> {
    Optional<DailyMetrics> findByTeamIdAndDate(UUID teamId, LocalDate date);
    List<DailyMetrics>findByTeamIdAndDateAfterOrderByDateAsc(UUID teamId, LocalDate after);

}
