package com.ftip.ftip.statemachine;
import com.ftip.ftip.entity.TestState;

public class StateHandlerFactory {
    public static TestStateHandler getHandler(TestState state) {
     return switch(state)
     {
         case HEALTHY -> new HealthyStateHandler();
         case SUSPECT -> new SuspectStateHandler();
         case FLAKY -> new FlakyStateHandler();
         case QUARANTINED -> new QuarantinedStateHandler();
         case RECOVERING -> new RecoveringStateHandler();
         case RETIRED -> throw new InvalidStateTransitionException("Retired testa cannot transition to any state");
     };
    }
}
