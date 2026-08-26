package com.ftip.ftip.service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ftip.ftip.entity.Quarantine;
import com.ftip.ftip.entity.StateTransitionLog;
import com.ftip.ftip.entity.TestIdentity;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.repository.QuarantineRepository;
import com.ftip.ftip.repository.StateTransitionLogRepository;
import com.ftip.ftip.repository.TestIdentityRepository;
import com.ftip.ftip.repository.TestRunRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    private final TestIdentityRepository testIdentityRepository;
    private final QuarantineRepository quarantineRepository;
    private final StateTransitionLogRepository stateTransitionLogRepository;
    private final TestRunRepository testRunRepository;

    @Scheduled(fixedRate=3600000)
    @Transactional
    public void checkQuarantinedTestsForRecovery()
    {
        log.info("Scheduler running-checking quarantined tests for recovery");
        List<TestIdentity>quarantinedTests=testIdentityRepository.findByCurrentState(TestState.QUARANTINED);
        for(TestIdentity test:quarantinedTests)
        {
            processQuarantinedTest(test);
        }
        log.info("Scheduler done-checked {} quarantined tests",quarantinedTests.size());
    }
    private void processQuarantinedTest(TestIdentity test)
    {
        Quarantine quarantine=quarantineRepository.findByTestIdentityId(test.getId()).orElse(null);
        if(quarantine==null)
        {
            return;
        }
        int requiredStreak=test.getTeam().getRecoveryStreakRequired();
        List<String> recentResults=testRunRepository.findRecentResultsForTest(test.getId(),requiredStreak);
        int passCount=0;
        for(String r:recentResults)
        {
            if("PASS".equals(r)) passCount++;
            else break;
        }
        quarantine.setConsecutivePasses(passCount);
        if(passCount>=requiredStreak)
        {
            quarantine.setRecoveryStartedAt(LocalDateTime.now());
            quarantineRepository.save(quarantine);
            StateTransitionLog transitionLog=new StateTransitionLog();
            transitionLog.setTestIdentity(test);
            transitionLog.setFromState(TestState.QUARANTINED);
            transitionLog.setToState(TestState.RECOVERING);
            transitionLog.setReason("Consecutive passes threshold reached: "+passCount+"/"+requiredStreak);
            transitionLog.setTriggeredBy("SCHEDULER");
            transitionLog.setScoreAtTransition(test.getFlakinessScore());
            stateTransitionLogRepository.save(transitionLog);
            test.setCurrentState(TestState.RECOVERING);
            testIdentityRepository.save(test);
            log.info("Test {} moved to Recovering after {} consecutive passes",test.getTestName(),passCount);
        }
        else
        {
            quarantineRepository.save(quarantine);
            log.info("Test {} has {}/{} passes- not ready for recovery",test.getTestName(),passCount,requiredStreak);
        }
    }
}
