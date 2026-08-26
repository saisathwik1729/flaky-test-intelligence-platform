package com.ftip.ftip.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.ftip.ftip.entity.Team;
import com.ftip.ftip.entity.TestState;

class StateHandlerTest {

    private final Team policy = new Team();

    @Test
    void healthyBecomesSuspectAboveThreshold() {
        assertEquals(TestState.SUSPECT,
                new HealthyStateHandler().onNewScore(45, policy));
    }

    @Test
    void healthyStaysHealthyBelowThreshold() {
        assertEquals(TestState.HEALTHY,
                new HealthyStateHandler().onNewScore(10, policy));
    }

    @Test
    void healthyRejectsOwnerApproval() {
        assertThrows(InvalidStateTransitionException.class,
                () -> new HealthyStateHandler().onOwnerApproval());
    }

    @Test
    void suspectBecomesFlakyAtFlakinessThreshold() {
        assertEquals(TestState.FLAKY,
                new SuspectStateHandler().onNewScore(70, policy));
    }

    @Test
    void flakyBecomesQuarantinedAtAutoThreshold() {
        assertEquals(TestState.QUARANTINED,
                new FlakyStateHandler().onNewScore(90, policy));
    }

    @Test
    void quarantinedIgnoresScoreEntirely() {
        assertEquals(TestState.QUARANTINED,
                new QuarantinedStateHandler().onNewScore(5, policy));
    }

    @Test
    void quarantinedRejectsSecondQuarantine() {
        assertThrows(InvalidStateTransitionException.class,
                () -> new QuarantinedStateHandler().onManualQuarantine());
    }

    @Test
    void quarantinedRecoversOnPassStreak() {
        assertEquals(TestState.RECOVERING,
                new QuarantinedStateHandler().onConsecutivePassesReached());
    }

    @Test
    void recoveringReturnsHealthyOnApproval() {
        assertEquals(TestState.HEALTHY,
                new RecoveringStateHandler().onOwnerApproval());
    }
}