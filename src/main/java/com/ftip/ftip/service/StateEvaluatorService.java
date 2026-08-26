package com.ftip.ftip.service;
import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ftip.ftip.entity.Quarantine;
import com.ftip.ftip.entity.StateTransitionLog;
import com.ftip.ftip.entity.TestIdentity;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.event.TestRunProcessedEvent;
import com.ftip.ftip.repository.QuarantineRepository;
import com.ftip.ftip.repository.StateTransitionLogRepository;
import com.ftip.ftip.repository.TestIdentityRepository;
import com.ftip.ftip.statemachine.StateHandlerFactory;
import com.ftip.ftip.statemachine.TestStateHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class StateEvaluatorService {
    private final TestIdentityRepository testIdentityRepository;
    private final StateTransitionLogRepository stateTransitionLogRepository;
    private final QuarantineRepository quarantineRepository;
    private final NotificationService notificationService;
    @EventListener
    @Transactional

    public void onTestRunProcessed(TestRunProcessedEvent event) {
        TestIdentity testIdentity = event.getTestIdentity();
        double newScore=event.getNewScore();
        TestState currentState=testIdentity.getCurrentState();
        TestStateHandler handler=StateHandlerFactory.getHandler(currentState);
        TestState nextState=handler.onNewScore(newScore,testIdentity.getTeam());
        if(!nextState.equals(currentState)){
            StateTransitionLog log=new StateTransitionLog();
            log.setTestIdentity(testIdentity);
            log.setFromState(currentState);
            log.setToState(nextState);
            log.setScoreAtTransition(newScore);
            log.setTriggeredBy("AUTO");
            log.setReason("Score "+newScore+" triggered transition from "+currentState+" to "+nextState);
            stateTransitionLogRepository.save(log);
            testIdentity.setCurrentState(nextState);
            testIdentityRepository.save(testIdentity);
                    testIdentity.setCurrentState(nextState);
            testIdentityRepository.save(testIdentity);

            if(nextState==TestState.QUARANTINED)
            {
                openQuarantine(testIdentity,"AUTO");
            }
            notificationService.notifyStateChange(testIdentity,currentState,nextState);
        }
    }

    private void openQuarantine(TestIdentity testIdentity, String by)
    {
        Quarantine quarantine=quarantineRepository
                .findByTestIdentityId(testIdentity.getId())
                .orElseGet(Quarantine::new);
        quarantine.setTestIdentity(testIdentity);
        quarantine.setQuarantinedAt(LocalDateTime.now());
        quarantine.setQuarantinedBy(by);
        quarantine.setConsecutivePasses(0);
        quarantine.setRecoveryStartedAt(null);
        quarantine.setRecoveredAt(null);
        quarantineRepository.save(quarantine);
    }
}
