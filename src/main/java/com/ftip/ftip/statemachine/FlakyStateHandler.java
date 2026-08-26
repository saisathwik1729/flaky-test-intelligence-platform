package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.Team;
import com.ftip.ftip.entity.TestState;

public class FlakyStateHandler implements TestStateHandler {
    @Override
    public TestState onNewScore(double newScore, Team policy) {
        if(newScore>=policy.getAutoQuarantineThreshold())
        {
            return TestState.QUARANTINED;
        }
        if(newScore<policy.getSuspectThreshold())
        {
            return TestState.HEALTHY;
        }
        return TestState.FLAKY;
    }
    @Override
    public TestState onManualQuarantine() {
        return TestState.QUARANTINED;
    }
    @Override
    public TestState onConsecutivePassesReached() {
        throw new InvalidStateTransitionException("Flaky test cannot recover-must be qurantined first");
    }
    @Override
    public TestState onOwnerApproval() {
        throw new InvalidStateTransitionException("Flaky does not need owner approval");
    }
    @Override
    public TestState getCurrentState() {
        return TestState.FLAKY;
    }
}
