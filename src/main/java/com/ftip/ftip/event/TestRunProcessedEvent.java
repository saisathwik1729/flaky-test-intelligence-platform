package com.ftip.ftip.event;
import com.ftip.ftip.entity.TestIdentity;
import com.ftip.ftip.entity.TestRun;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TestRunProcessedEvent extends ApplicationEvent{
    private final TestIdentity testIdentity;
    private final TestRun testRun;
    private final double newScore;
    public TestRunProcessedEvent(Object source, TestIdentity testidentity, TestRun testRun, double newScore) {
        super(source);
        this.testIdentity=testidentity;
        this.testRun=testRun;
        this.newScore=newScore;
    }
}
