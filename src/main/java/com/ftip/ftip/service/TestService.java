package com.ftip.ftip.service;
import com.ftip.ftip.dto.StateTransitionResponse;
import com.ftip.ftip.dto.TestIdentityResponse;
import com.ftip.ftip.dto.TestRunResponse;
import com.ftip.ftip.entity.TestIdentity;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.repository.StateTransitionLogRepository;
import com.ftip.ftip.repository.TestIdentityRepository;
import com.ftip.ftip.repository.TestRunRepository;
import com.ftip.ftip.statemachine.InvalidStateTransitionException;
import com.ftip.ftip.statemachine.StateHandlerFactory;
import com.ftip.ftip.entity.StateTransitionLog;
import com.ftip.ftip.statemachine.TestStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService
{
    private final TestIdentityRepository testIdentityRepository;
    private final TestRunRepository testRunRepository;
    private final StateTransitionLogRepository stateTransitionLogRepository;
    public List<TestIdentityResponse>getTestsByTeam(UUID teamId)
    {
        return testIdentityRepository.findByTeamId(teamId).stream().map(this::toResponse).collect(Collectors.toList());
    }
    public List<TestIdentityResponse>getTestsByState(TestState state)
    {
        return testIdentityRepository.findByCurrentState(state).stream().map(this::toResponse).collect(Collectors.toList());
    }
    public TestIdentityResponse getTestById(UUID id)
    {
        TestIdentity test=testIdentityRepository.findById(id).orElseThrow(()->new RuntimeException("Test not found: "+id));
        return toResponse(test);
    }
    public List<TestRunResponse>getRunsForTest(UUID testId)
    {
        return testRunRepository.findByTestIdentityIdOrderByRunAtDesc(testId).stream().map(run->{
            TestRunResponse r=new TestRunResponse();
            r.setId(run.getId());
            r.setResult(run.getResult());
            r.setDurationMs(run.getDurationMs());
            r.setBranch(run.getBranch());
            r.setCommitSha(run.getCommitSha());
            r.setEnvironment(run.getEnvironment());
            r.setRunAt(run.getRunAt());
            return r;
        }).collect(Collectors.toList());
    }
    public List<StateTransitionResponse>getTransitionsForTest(UUID testId)
    {
        return stateTransitionLogRepository.findByTestIdentityIdOrderByTransitionedAtDesc(testId).stream().map(log->{
            StateTransitionResponse r=new StateTransitionResponse();
            r.setId(log.getId());
            r.setFromState(log.getFromState()!=null ? log.getFromState().name() : null);
            r.setToState(log.getToState()!=null ? log.getToState().name() : null);
            r.setReason(log.getReason());
            r.setTriggeredBy(log.getTriggeredBy());
            r.setScoreAtTransition(log.getScoreAtTransition());
            r.setTransitionedAt(log.getTransitionedAt());
            return r;
        }).collect(Collectors.toList());
    }
    @Transactional
    public TestIdentityResponse manualQuarantine(UUID testId)
    {
        TestIdentity test=testIdentityRepository.findById(testId).orElseThrow(()->new RuntimeException("Test not found: "+testId));
        TestState currentState=test.getCurrentState();
        TestState nextState=StateHandlerFactory.getHandler(currentState).onManualQuarantine();
        logTransition(test,currentState,nextState,"Manual quarantine by admin","ADMIN");
        test.setCurrentState(nextState);
        return toResponse(testIdentityRepository.save(test));
    }
    @Transactional
    public TestIdentityResponse approveRecovery(UUID testId)
    {
        TestIdentity test=testIdentityRepository.findById(testId).orElseThrow(()->new RuntimeException("Test not found: "+testId));
        TestState currentState=test.getCurrentState();
        TestState nextState=StateHandlerFactory.getHandler(currentState).onOwnerApproval();
        logTransition(test,currentState,nextState,"Owner approved recovery","OWNER");
        test.setCurrentState(nextState);
        return toResponse(testIdentityRepository.save(test));
    }
    private void logTransition(TestIdentity test, TestState from, TestState to, String reason, String triggeredBy)
    {
        StateTransitionLog log=new StateTransitionLog();
        log.setTestIdentity(test);
        log.setFromState(from);
        log.setToState(to);
        log.setReason(reason);
        log.setTriggeredBy(triggeredBy);
        log.setScoreAtTransition(test.getFlakinessScore());
        stateTransitionLogRepository.save(log);
    }
    private TestIdentityResponse toResponse(TestIdentity test)
    {
        TestIdentityResponse r=new TestIdentityResponse();
        r.setId(test.getId());
        r.setTestName(test.getTestName());
        r.setTestClass(test.getTestClass());
        r.setOwnerEmail(test.getOwnerEmail());
        r.setCurrentState(test.getCurrentState().name());
        r.setFlakinessScore(test.getFlakinessScore());
        r.setLastEvaluationAt(test.getLastEvaluatedAt());
        r.setCreatedAt(test.getCreatedAt());
        r.setTeamName(test.getTeam().getName());
        return r;
    }
}
