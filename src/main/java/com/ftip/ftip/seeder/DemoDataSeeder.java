package com.ftip.ftip.seeder;
import com.ftip.ftip.entity.*;
import com.ftip.ftip.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@Profile("demo")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {
    private final TeamRepository teamRepository;
    private final TestIdentityRepository testIdentityRepository;
    private final TestRunRepository testRunRepository;
    private final StateTransitionLogRepository stateTransitionLogRepository;
    private final QuarantineRepository quarantineRepository;

    @Override
    @Transactional

    public void run(String... args)
    {
        if(teamRepository.count()>0)
        {
            log.info("Demo data already exists -skipping seeder");
            return;
        }
        log.info("Seeding demo data...");
        Team paymentsTeam=createTeam("Payments Team","https://github.com/example/payments");
        Team searchTeam=createTeam("Search Team","https://github.com/example/search");
        createHealthyTest(paymentsTeam,"PaymentTest.testSuccessfulPayment","PaymentTest");
        createHealthyTest(paymentsTeam,"PaymentTest.testRefundFlow","PaymentTest");
        createSuspectTest(paymentsTeam,"PaymentTest.testTimeoutHandling","PaymentTest");
        createFlakyTest(paymentsTeam,"PaymentTest.testConcurrentPayments","PaymentTest");
        createQuarantinedTest(paymentsTeam,"PaymentTest.testExternalGateway","PaymentTest");

        createHealthyTest(searchTeam,"SearchTest.testBasicSearch","SearchTest");
        createFlakyTest(searchTeam,"SearchTest.testSearchingRanking","SearchTest");
        createQuarantinedTest(searchTeam,"SearchTest.testAutoComplete","SearchTest");
        createRecoveringTest(searchTeam,"SearchTest.testSpellCheck","SearchTest");

        log.info("Demo data seeded successfully-2 teams, 9 tests");
    }
    private Team createTeam(String name,String repoUrl)
    {
        Team team=new Team();
        team.setName(name);
        team.setRepoUrl(repoUrl);
        team.setFlakinessThreshold(60);
        team.setAutoQuarantineThreshold(85);
        team.setRecoveryStreakRequired(10);
        team.setScoringWindowDays(30);
        return teamRepository.save(team);
    }
    private void createHealthyTest(Team team,String testName, String testClass)
    {
        TestIdentity test=buildTest(team,testName,testClass,TestState.HEALTHY,8.5);
        for(int i=20; i>=1; i--)
        {
            createRun(test,"PASS",800,LocalDateTime.now().minusDays(i));
        }
    }
    private void createSuspectTest(Team team, String testName, String testClass)
    {
        TestIdentity test=buildTest(team,testName,testClass,TestState.SUSPECT,45.0);
        for(int i=30; i>=1; i--)
        {
            String result=(i<=5 && i%2==0)?"FAIL":"PASS";
            createRun(test,result,1100,LocalDateTime.now().minusDays(i));
        }
        logTransition(test,null,TestState.HEALTHY,TestState.SUSPECT,"Score 45.0 triggered transition", 45.0);
    }
    private void createFlakyTest(Team team,String testName, String testClass)
    {
        TestIdentity test=buildTest(team,testName,testClass,TestState.FLAKY,72.0);
        Random rand=new Random(42);
        for(int i=30; i>=1; i--)
        {
            String result=rand.nextInt(10)<4?"FAIL":"PASS";
            createRun(test,result,1500,LocalDateTime.now().minusDays(i));
        }
        logTransition(test,null,TestState.HEALTHY,TestState.SUSPECT,"Score 42.0 triggered transition",42.0);
        logTransition(test,null,TestState.SUSPECT,TestState.FLAKY,"Score 72.0 triggered transition", 72.0);
    }
    private void createQuarantinedTest(Team team,String testName, String testClass)
    {
        TestIdentity test=buildTest(team,testName,testClass,TestState.QUARANTINED,88.0);
        for(int i=30; i>=1; i--)
        {
            String result=i%5==0?"PASS":"FAIL";
            createRun(test,result,2000,LocalDateTime.now().minusDays(i));
        }
        logTransition(test,null,TestState.HEALTHY,TestState.SUSPECT,"Score 50.0 triggered transition",50.0);
        logTransition(test,null,TestState.SUSPECT,TestState.FLAKY,"Score triggered transition",75.0);
        logTransition(test,null,TestState.FLAKY,TestState.QUARANTINED,"Score 88.0 exceeded auto-quarantine threshold",88.0);

        Quarantine quarantine=new Quarantine();
        quarantine.setTestIdentity(test);
        quarantine.setQuarantinedBy("AUTO");
        quarantine.setConsecutivePasses(0);
        quarantineRepository.save(quarantine);
    }
    private void createRecoveringTest(Team team, String testName, String testClass)
    {
        TestIdentity test=buildTest(team,testName,testClass, TestState.RECOVERING,88.0);
        for(int i=30; i>=11; i--)
        {
            createRun(test,"FAIL",1800,LocalDateTime.now().minusDays(i));
        }
        for(int i=10; i>=1; i--)
        {
            createRun(test,"PASS",900,LocalDateTime.now().minusDays(i));
        }
        logTransition(test,null,TestState.FLAKY,TestState.QUARANTINED,"Auto quarantined",88.0);
        logTransition(test,null,TestState.QUARANTINED,TestState.RECOVERING,"10 consecutive passes reached",88.0);

        Quarantine quarantine=new Quarantine();
        quarantine.setTestIdentity(test);
        quarantine.setQuarantinedBy("AUTO");
        quarantine.setConsecutivePasses(10);
        quarantine.setRecoveryStartedAt(LocalDateTime.now().minusDays(10));
        quarantineRepository.save(quarantine);
    }
    private TestIdentity buildTest(Team team, String testName, String testClass, TestState state, double score)
    {
        TestIdentity test=new TestIdentity();
        test.setTeam(team);
        test.setTestName(testName);
        test.setTestClass(testClass);
        test.setOwnerEmail("dev@"+team.getName().toLowerCase().replace(" "," ")+".com");
        test.setCurrentState(state);
        test.setFlakinessScore(score);
        test.setLastEvaluatedAt(LocalDateTime.now());
        return testIdentityRepository.save(test);
    }
    private void createRun(TestIdentity test,String result,long durationMs,LocalDateTime runAt)
    {
        TestRun run=new TestRun();
        run.setTestIdentity(test);
        run.setResult(result);
        run.setDurationMs(durationMs);
        run.setBranch("main");
        run.setCommitSha("demo"+System.nanoTime());
        run.setEnvironment("linux");
        run.setRunAt(runAt);
        testRunRepository.save(run);
    }
    private void logTransition(TestIdentity test, Object unused, TestState from, TestState to, String reason, double score)
    {
        StateTransitionLog log=new StateTransitionLog();
        log.setTestIdentity(test);
        log.setFromState(from);
        log.setToState(to);
        log.setReason(reason);
        log.setTriggeredBy("AUTO");
        log.setScoreAtTransition(score);
        stateTransitionLogRepository.save(log);
    }
}
