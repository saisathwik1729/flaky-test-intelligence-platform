package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.TestState;

public class RecoveringStateHandler implements TestStateHandler{
    @Override
    public TestState onNewScore(double newScore)
    {
        return TestState.RECOVERING;
    }
    @Override
    public TestState onManualQuarantine()
    {
        return TestState.QUARANTINED;
    }
    @Override
    public TestState onConsecutivePassesReached()
    {
        return TestState.RECOVERING;
    }
    @Override
    public TestState onOwnerApproval()
    {
        return TestState.HEALTHY;
    }
    @Override
    public TestState getCurrentState()
    {
        return TestState.RECOVERING;
    }
}
