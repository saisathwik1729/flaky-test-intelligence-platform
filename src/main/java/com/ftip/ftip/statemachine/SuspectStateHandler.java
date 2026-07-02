package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.TestState;


public class SuspectStateHandler implements TestStateHandler{
    @Override
    public TestState onNewScore(double newScore)
    {
        if(newScore>=60)
        {
            return TestState.FLAKY;
        }
        if(newScore<40)
        {
            return TestState.HEALTHY;
        }
        return TestState.SUSPECT;
    }

    @Override
    public TestState onManualQuarantine()
    {
        return TestState.QUARANTINED;
    }
    @Override
    public TestState onConsecutivePassesReached()
    {
        throw new InvalidStateTransitionException("Suspect test cannot recover-not qurantined yet");
    }
    @Override
    public TestState onOwnerApproval()
    {
        throw new InvalidStateTransitionException("Suspect test do not need owner approval");
    }

    @Override
    public TestState getCurrentState() {
        return TestState.SUSPECT;
    }
}
