package com.ftip.ftip.scoring;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ftip.ftip.entity.TestRun;

class WeightedFlakinessScoringStrategyTest {

    private final WeightedFlakinessScoringStrategy strategy =
            new WeightedFlakinessScoringStrategy();

    private List<TestRun> runs(String... results) {
        List<TestRun> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < results.length; i++) {
            TestRun r = new TestRun();
            r.setResult(results[i]);
            r.setRunAt(now.minusHours(i));
            list.add(r);
        }
        return list;
    }

    @Test
    void allPassesScoresZero() {
        assertEquals(0.0, strategy.calculate(
                runs("PASS","PASS","PASS","PASS","PASS")), 0.01);
    }

        @Test
    void tenConsecutiveFailuresScoresMaximum() {
        double score = strategy.calculate(
                runs("FAIL","FAIL","FAIL","FAIL","FAIL",
                     "FAIL","FAIL","FAIL","FAIL","FAIL"));
        assertEquals(100.0, score, 0.01);
    }

    @Test
    void shortFailStreakScoresBelowMaximum() {
        double score = strategy.calculate(
                runs("FAIL","FAIL","FAIL","FAIL","FAIL"));
        assertEquals(87.5, score, 0.01);
    }

    @Test
    void emptyHistoryScoresZero() {
        assertEquals(0.0, strategy.calculate(new ArrayList<>()), 0.01);
    }

    @Test
    void recentFailuresOutweighOldFailures() {
        double recent = strategy.calculate(
                runs("FAIL","FAIL","FAIL","PASS","PASS","PASS"));
        double old = strategy.calculate(
                runs("PASS","PASS","PASS","FAIL","FAIL","FAIL"));
        assertTrue(recent > old,
                "recent=" + recent + " should exceed old=" + old);
    }
}