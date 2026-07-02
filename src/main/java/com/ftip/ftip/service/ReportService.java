package com.ftip.ftip.service;
import com.ftip.ftip.dto.FlakinessSummaryResponse;
import com.ftip.ftip.dto.TestIdentityResponse;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.repository.DailyMetricsRepository;
import com.ftip.ftip.repository.TeamRepository;
import com.ftip.ftip.repository.TestIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TestIdentityRepository testIdentityRepository;
    private final DailyMetricsRepository dailyMetricsRepository;
    private final TeamRepository teamRepository;
    public FlakinessSummaryResponse getTeamSummary(UUID teamId)
    {
        teamRepository.findById(teamId).orElseThrow(()->new RuntimeException("Team not found: "+teamId));
        var allTests=testIdentityRepository.findByTeamId(teamId);
        double totalWasted=dailyMetricsRepository.findByTeamIdAndDateAfterOrderByDateAsc(teamId,LocalDate.now().minusDays(30)).stream().mapToDouble(m->m.getCiMinutesWasted()).sum();
        FlakinessSummaryResponse summary=new FlakinessSummaryResponse();
        summary.setTeamId(teamId);
        summary.setTeamName(teamRepository.findById(teamId).get().getName());
        summary.setTotalTests(allTests.size());
        summary.setHealthyCount(countByState(allTests.stream().map(t->t.getCurrentState()).collect(Collectors.toList()),TestState.HEALTHY));
        summary.setSuspectCount(countByState(allTests.stream().map(t->t.getCurrentState()).collect(Collectors.toList()),TestState.SUSPECT));
        summary.setFlakyCount(countByState(allTests.stream().map(t -> t.getCurrentState()).collect(Collectors.toList()), TestState.FLAKY));
        summary.setQuarantinedCount(countByState(allTests.stream().map(t -> t.getCurrentState()).collect(Collectors.toList()), TestState.QUARANTINED));
        summary.setRecoveringCount(countByState(allTests.stream().map(t -> t.getCurrentState()).collect(Collectors.toList()), TestState.RECOVERING));
        summary.setTotalCiMinutesWasted(totalWasted);
        return summary;
    }
    @Transactional
    public List<TestIdentityResponse>getFlakyLeaderboard(UUID teamId)
    {
        return testIdentityRepository.findByTeamId(teamId)
                .stream()
                .filter(t -> t.getCurrentState() == TestState.FLAKY
                        || t.getCurrentState() == TestState.QUARANTINED)
                .sorted((a, b) -> Double.compare(
                        b.getFlakinessScore(), a.getFlakinessScore()))
                .limit(10)
                .map(t -> {
                    TestIdentityResponse r = new TestIdentityResponse();
                    r.setId(t.getId());
                    r.setTestName(t.getTestName());
                    r.setCurrentState(t.getCurrentState().name());
                    r.setFlakinessScore(t.getFlakinessScore());
                    r.setTeamName(t.getTeam().getName());
                    return r;
                })
                .collect(Collectors.toList());
    }
    public int countByState(List<TestState>states,TestState target)
    {
        return (int) states.stream().filter(s->s==target).count();
    }
}
