package com.ftip.ftip.service;
import com.ftip.ftip.entity.DailyMetrics;
import com.ftip.ftip.entity.TestRun;
import com.ftip.ftip.event.TestRunProcessedEvent;
import com.ftip.ftip.repository.DailyMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor

public class MetricUpdaterService {
    private final DailyMetricsRepository dailyMetricsRepository;
    @EventListener
    @Transactional
    public void onTestRunProcessedEvent(TestRunProcessedEvent event) {
        TestRun testRun = event.getTestRun();
        java.util.UUID teamId=event.getTestIdentity().getTeam().getId();
        LocalDate today = LocalDate.now();
        DailyMetrics metrics=dailyMetricsRepository.findByTeamIdAndDate(teamId, today).orElseGet(()->{
            DailyMetrics newMetrics = new DailyMetrics();
            newMetrics.setTeam(event.getTestIdentity().getTeam());
            newMetrics.setDate(today);
            return newMetrics;
        });
        metrics.setTotalRuns(metrics.getTotalRuns() + 1);
        if("FAIL".equals(testRun.getResult()))
        {
            metrics.setTotalRuns(metrics.getTotalRuns() + 1);
            double minutesWasted=testRun.getDurationMs()/60000.0;
            metrics.setCiMinutesWasted(metrics.getCiMinutesWasted() + minutesWasted);
        }
        dailyMetricsRepository.save(metrics);
    }

}
