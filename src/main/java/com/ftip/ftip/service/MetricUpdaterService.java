package com.ftip.ftip.service;
import java.time.LocalDate;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ftip.ftip.entity.DailyMetrics;
import com.ftip.ftip.entity.TestRun;
import com.ftip.ftip.event.TestRunProcessedEvent;
import com.ftip.ftip.repository.DailyMetricsRepository;

import lombok.RequiredArgsConstructor;

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
            metrics.setTotalFailures(metrics.getTotalFailures() + 1);
            metrics.setCiMinutesWasted(metrics.getCiMinutesWasted() + (testRun.getDurationMs()/60000.0));
        }
        dailyMetricsRepository.save(metrics);
    }

}
