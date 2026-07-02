package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.TestState;
import org.aspectj.weaver.ast.Test;

public class FlakyStateHandler implements TestStateHandler {
    @Override
    public TestState onNewScore(double newScore) {
        if(newScore>=85)
        {
            return TestState.QUARANTINED;
        }
        if(newScore<40)
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
