package com.ftip.ftip.scoring;
import com.ftip.ftip.entity.TestRun;
import java.util.List;

public interface FlakinessScoringStrategy {
    double calculate(List<TestRun>recentRuns);
}
