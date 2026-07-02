package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.TestState;

public class QuarantinedStateHandler implements TestStateHandler{
    @Override
    public TestState onNewScore(double newScore) {
        return TestState.QUARANTINED;
    }
    @Override
    public TestState onManualQuarantine()
    {
        throw new InvalidStateTransitionException("Test is already Quarantined");
    }
    @Override
    public TestState onConsecutivePassesReached()
    {
        return TestState.RECOVERING;
    }
    @Override
    public TestState onOwnerApproval()
    {
        throw new InvalidStateTransitionException("Quarantined test needs consecutive passes first before owner approval");
    }
    @Override
    public TestState getCurrentState() {
        return TestState.QUARANTINED;
    }
}
