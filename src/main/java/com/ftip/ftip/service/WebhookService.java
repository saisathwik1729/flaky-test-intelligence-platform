package com.ftip.ftip.service;
import com.ftip.ftip.dto.TestResultRequest;
import com.ftip.ftip.dto.WebhookPayloadRequest;
import com.ftip.ftip.entity.TestIdentity;
import com.ftip.ftip.entity.TestRun;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.event.TestRunProcessedEvent;
import com.ftip.ftip.repository.TeamRepository;
import com.ftip.ftip.repository.TestIdentityRepository;
import com.ftip.ftip.repository.TestRunRepository;
import com.ftip.ftip.scoring.FlakinessScoringStrategy;
import com.ftip.ftip.scoring.WeightedFlakinessScoringStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor


public class WebhookService {
    private final TeamRepository teamRepository;
    private final TestIdentityRepository testIdentityRepository;
    private final TestRunRepository testRunRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisService redisService;
    private final FlakinessScoringStrategy scoringStrategy=new WeightedFlakinessScoringStrategy();

    @Transactional
    public void processWebhook(WebhookPayloadRequest payload) {
        teamRepository.findById(payload.getTeamId()).orElseThrow(()->new RuntimeException("Team not found: "+payload.getTeamId()));
        for(TestResultRequest result: payload.getResults())
        {
            processOneTestResult(payload,result);
        }
    }
    private void processOneTestResult(WebhookPayloadRequest payload, TestResultRequest result) {
        TestIdentity testIdentity=findOrCreateTestIdentity(payload,result);
        TestRun testRun=new TestRun();
        testRun.setTestIdentity(testIdentity);
        testRun.setResult(result.getResult());
        testRun.setDurationMs(result.getDurationMs());
        testRun.setBranch(payload.getBranch());
        testRun.setCommitSha(payload.getCommitSha());
        testRun.setEnvironment(payload.getEnvironment());
        testRunRepository.save(testRun);

        redisService.invalidateTestRuns(testIdentity.getId());

        List<TestRun>recentRuns=redisService.getCachedTestRuns(testIdentity.getId());

        if(recentRuns==null)
        {
            recentRuns=testRunRepository.findByTestIdentityIdAndRunAtAfterOrderByRunAtDesc(testIdentity.getId(),LocalDateTime.now().minusDays(30));
            redisService.cacheTestRuns(testIdentity.getId(),recentRuns);
        }
        double newScore=scoringStrategy.calculate(recentRuns);
        testIdentity.setFlakinessScore(newScore);
        testIdentity.setLastEvaluatedAt(LocalDateTime.now());
        testIdentityRepository.save(testIdentity);
        eventPublisher.publishEvent(new TestRunProcessedEvent(this,testIdentity,testRun,newScore));
    }
    private TestIdentity findOrCreateTestIdentity(WebhookPayloadRequest payload, TestResultRequest result) {
        return testIdentityRepository.findByTeamIdAndTestName(payload.getTeamId(),result.getTestName()).orElseGet(()->{
            TestIdentity newIdentity=new TestIdentity();
            newIdentity.setTeam(teamRepository.findById(payload.getTeamId()).get());
            newIdentity.setTestName(result.getTestName());
            newIdentity.setTestClass(result.getTestClass());
            newIdentity.setOwnerEmail(result.getOwnerEmail());
            newIdentity.setCurrentState(TestState.HEALTHY);
            newIdentity.setFlakinessScore(0.0);
            return testIdentityRepository.save(newIdentity);
        });
    }
}
