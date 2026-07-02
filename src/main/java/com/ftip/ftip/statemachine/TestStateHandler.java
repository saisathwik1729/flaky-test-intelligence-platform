package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.TestState;

public interface TestStateHandler {
    TestState onNewScore(double newScore);
    TestState onManualQuarantine();
    TestState onConsecutivePassesReached();
    TestState onOwnerApproval();
    TestState getCurrentState();
}
