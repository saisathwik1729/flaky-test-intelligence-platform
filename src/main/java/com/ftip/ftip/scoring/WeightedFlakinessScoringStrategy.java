package com.ftip.ftip.scoring;
import com.ftip.ftip.entity.TestRun;
import java.util.List;

public class WeightedFlakinessScoringStrategy implements FlakinessScoringStrategy {
    @Override
    public double calculate(List<TestRun> recentRuns) {
        if(recentRuns==null || recentRuns.isEmpty())
        {
            return 0.0;
        }
        double passRateScore=calculatePassRateScore(recentRuns);
        double streakScore=calculateStreakScore(recentRuns);
        double recencyScore=calculateRecencyScore(recentRuns);
        double totalScore=(passRateScore*0.40)+(streakScore*0.25)+(recencyScore*0.35);
        return Math.min(Math.round(totalScore*100.0)/100.0,100.0);
    }
    private double calculatePassRateScore(List<TestRun> runs) {
        long failCount=runs.stream().filter(r->"FAIL".equals(r.getResult())).count();
        return ((double)failCount/runs.size())*100.0;
    }
    private double calculateStreakScore(List<TestRun> runs) {
        int maxStreak=0;
        int currentStreak=0;
        for(TestRun run:runs)
        {
            if("FAIL".equals(run.getResult()))
            {
                currentStreak++;
                maxStreak=Math.max(maxStreak,currentStreak);
            }
            else
            {
                currentStreak=0;
            }
        }
        return Math.min(maxStreak*10.0,100.0);
    }
    private double calculateRecencyScore(List<TestRun> runs) {
        int size=runs.size();
        double weightedFails=0.0;
        double totalWeight=0.0;
        for(int i=0; i<size; i++)
        {
            double weight=(i+1);
            totalWeight+=weight;
            if("FAIL".equals(runs.get(i).getResult()))
            {
                weightedFails += weight;
            }
        }
        return totalWeight==0?0.0:(weightedFails/totalWeight)*100.0;
    }
}
