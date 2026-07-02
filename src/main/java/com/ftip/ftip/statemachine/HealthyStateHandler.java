package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.TestState;


public class HealthyStateHandler implements TestStateHandler{
    @Override
    public TestState onNewScore(double newScore)
    {
        if(newScore>=49)
        {
            return TestState.SUSPECT;
        }
        return TestState.HEALTHY;
    }
    @Override
    public TestState onManualQuarantine()
    {
        return TestState.QUARANTINED;
    }
    @Override
    public TestState onConsecutivePassesReached()
    {
        throw new InvalidStateTransitionException("Healthy test cannot transition via consecutive passes- already healthy");
    }
    @Override
    public  TestState onOwnerApproval()
    {
        throw new InvalidStateTransitionException("Healthy Test does not need owner approval");
    }
    @Override
    public TestState getCurrentState()
    {
        return TestState.HEALTHY;
    }
}
