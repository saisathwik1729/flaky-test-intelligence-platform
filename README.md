Flaky Test Intelligence Platform (FTIP)

A CI-integrated backend system that automatically detects, scores, and quarantines flaky tests — so engineering teams stop wasting time re-running random CI failures.


The Problem

Every software team has flaky tests — tests that randomly fail even when the code is unchanged. Developers learn to ignore them and just re-run CI. Over time:


Engineers waste 2–4 hours per week on re-runs
Confidence in CI drops — real bugs get ignored too
Nobody fixes the root cause because nobody tracks which tests are flaky


FTIP solves this by being the memory of your CI pipeline.


What It Does


GitHub Actions finishes a test run
FTIP receives results via webhook
Calculates a flakiness score (0–100) per test
Transitions test state: HEALTHY → SUSPECT → FLAKY → QUARANTINED
Auto-quarantines tests above threshold (score > 85)
Notifies the test owner
Monitors recovery — re-enables test after 10 consecutive passes



Architecture

[GitHub Actions] --POST /api/webhook/ci--> [Spring Boot App]
[Postman / UI]  --REST API calls---------> [Spring Boot App]

Inside Spring Boot:
WebhookController
|
WebhookService  (scoring + save + publish event)
|
Spring Events
|-- StateEvaluatorService  (state machine transitions)
|-- MetricsUpdaterService  (daily CI waste tracking)
|
@Scheduled Jobs (hourly quarantine recovery check)
|
PostgreSQL (main store) + Redis (run history cache)


LLD Design Patterns Used

State Pattern — Test Health Lifecycle

Each test moves through 5 states. Every state is its own class implementing TestStateHandler. Invalid transitions throw InvalidStateTransitionException — no silent corruption.

HEALTHY --> SUSPECT --> FLAKY --> QUARANTINED --> RECOVERING --> HEALTHY

java// Invalid transition — caught at the state level
HealthyStateHandler.onConsecutivePassesReached()
// throws InvalidStateTransitionException
// Healthy tests do not need recovery — this call makes no sense

Strategy Pattern — Flakiness Scoring

FlakinessScoringStrategy interface with WeightedFlakinessScoringStrategy as the default. Different teams can plug in different scoring models without touching the service layer.

Weighted score formula (0–100):

FactorWeightReasonPass rate over 30 days40%Most important signalLongest consecutive fail streak25%Sustained failures are alarmingRecency bias (recent failures weighted 2x)35%Last week matters more than last month

Observer Pattern — Decoupled Event Listeners

When a test run arrives, WebhookService publishes a TestRunProcessedEvent. Two independent listeners react:


StateEvaluatorService — checks if state should change
MetricsUpdaterService — updates daily CI waste metrics


Neither listener knows about the other. Adding a new listener tomorrow requires zero changes to existing code.

Builder Pattern — Policy Configuration

FlakinessPolicy is built via FlakinessPolicyBuilder with validation at build time. No invalid policies can exist at runtime.


Tech Stack

LayerTechnologyLanguageJava 21FrameworkSpring Boot 3.5.14Build toolMavenDatabasePostgreSQL 17CacheRedis (Memurai on Windows)ORMSpring Data JPA + Hibernate 6EventsSpring ApplicationEventPublisherSchedulerSpring @ScheduledValidationJakarta Validation


Database Schema

TablePurposeteamWho owns the teststest_identityUnique test with current state and scoretest_runEvery CI run result (permanent history)state_transition_logAudit trail of every state changequarantineQuarantine tracking and recovery progressnotification_logProof of every alert sentdaily_metricsPre-aggregated daily summaries for reports


API Reference

Webhook

MethodEndpointDescriptionPOST/api/webhook/ciReceive test run results from CI

Teams

MethodEndpointDescriptionPOST/api/teamsCreate a teamGET/api/teamsList all teamsGET/api/teams/{id}Get team by ID

Tests

MethodEndpointDescriptionGET/api/tests?teamId=Get all tests for a teamGET/api/tests?state=FLAKYFilter tests by stateGET/api/tests/{id}Get test with score and current stateGET/api/tests/{id}/runsGet full run history (newest first)GET/api/tests/{id}/transitionsGet state transition audit trailPOST/api/tests/{id}/quarantineManual quarantine overridePOST/api/tests/{id}/approve-recoveryOwner approves test recovery

Reports

MethodEndpointDescriptionGET/api/reports/summary?teamId=Team health snapshotGET/api/reports/flaky-leaderboard?teamId=Top flaky tests ranked by score


Running Locally

Prerequisites


Java 21
Maven
PostgreSQL 17 running on port 5432
Redis or Memurai running on port 6379


Setup

1. Clone the repo

bashgit clone https://github.com/saisathwik1729/flaky-test-intelligence-platform.git
cd flaky-test-intelligence-platform

2. Create database

sqlCREATE DATABASE ftip;

3. Configure credentials

Edit src/main/resources/application.properties:

propertiesspring.datasource.url=jdbc:postgresql://localhost:5432/ftip
spring.datasource.username=postgres
spring.datasource.password=your_password

4. Run normally

bashmvn spring-boot:run

Tables are auto-created by JPA on first run.

5. Run with demo data

bashmvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=demo"

Seeds 2 teams and 9 tests across all states — ready to demo immediately.


Demo Walkthrough

After running with demo profile:

Step 1 — See all teams

GET http://localhost:8080/api/teams

Step 2 — Get flaky leaderboard (use Payments Team ID from step 1)

GET http://localhost:8080/api/reports/flaky-leaderboard?teamId={id}

Shows QUARANTINED (score 88) and FLAKY (score 72) tests ranked by severity.

Step 3 — Get team health summary

GET http://localhost:8080/api/reports/summary?teamId={id}

Shows: 2 healthy, 1 suspect, 1 flaky, 1 quarantined.

Step 4 — Drill into a flaky test

GET http://localhost:8080/api/tests/{test-id}/transitions

Shows the full audit trail: HEALTHY → SUSPECT → FLAKY with exact scores and timestamps.

Step 5 — Simulate a new CI run

POST http://localhost:8080/api/webhook/ci

json{
"teamId": "paste-payments-team-id-here",
"branch": "main",
"commitSha": "abc123",
"environment": "linux",
"results": [
{
"testName": "PaymentTest.testRefund",
"testClass": "PaymentTest",
"result": "FAIL",
"durationMs": 1240,
"ownerEmail": "dev@example.com"
}
]
}

Watch the score update and state transition happen in real time.


Key Interview Talking Points

"How did you handle concurrency when two CI runs arrive at the same time?"

Redis cache invalidation on every new run prevents inconsistent scoring. JPA optimistic locking on test_identity is the second safety net — concurrent updates throw OptimisticLockException instead of silently corrupting data.

"Walk me through a design pattern you used and why."

State Pattern on TestStateHandler. Each state enforces its own valid transitions. HealthyState.onConsecutivePassesReached() throws — healthy tests do not need recovery. No if-else chains anywhere. The state machine cannot be put into an invalid state by any code path.

"How does the scoring algorithm work?"

Weighted formula across three factors: pass rate (40%), fail streak (25%), recency-weighted failures (35%). Recency bias means a test that failed 5 times last week scores higher than one that failed 5 times last month — which reflects real risk more accurately.

"Why Spring Events instead of direct service calls?"

When a test run arrives, three things need to happen: score update, state evaluation, metrics update. With direct calls, WebhookService would depend on three other services and know about all of them. With Spring Events, it publishes one event and knows nothing about who reacts. Adding a notification listener tomorrow requires zero changes to WebhookService.


Project Structure

src/main/java/com/ftip/ftip/
├── controller/       HTTP layer — WebhookController, TestController, TeamController, ReportController
├── service/          Business logic — WebhookService, StateEvaluatorService, MetricsUpdaterService
├── repository/       Database access — 6 Spring Data JPA repositories
├── entity/           7 JPA entities mapping to PostgreSQL tables
├── dto/              Request/Response objects — clean API contracts
├── event/            Spring event classes — TestRunProcessedEvent
├── statemachine/     State Pattern — 5 state handlers + factory + exception
├── scoring/          Strategy Pattern — FlakinessScoringStrategy + WeightedImpl
├── config/           Redis configuration
└── seeder/           Demo data seeder (@Profile demo)


Author

Built by Sai Sathwik — Final Year B.Tech, PDPM IIITDM Jabalpur