package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.Team;
import com.ftip.ftip.entity.TestState;

public interface TestStateHandler {
    TestState onNewScore(double newScore, Team policy);
    TestState onManualQuarantine();
    TestState onConsecutivePassesReached();
    TestState onOwnerApproval();
    TestState getCurrentState();
}
