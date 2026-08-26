package com.ftip.ftip.controller;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ftip.ftip.dto.StateTransitionResponse;
import com.ftip.ftip.dto.TestIdentityResponse;
import com.ftip.ftip.dto.TestRunResponse;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    @GetMapping
    public ResponseEntity<List<TestIdentityResponse>>getTests(@RequestParam(required=false)UUID teamId, @RequestParam(required=false)TestState state)
    {
        if(teamId!=null)
        {
            return ResponseEntity.ok(testService.getTestsByTeam(teamId));
        }
        if(state!=null)
        {
            return ResponseEntity.ok(testService.getTestsByState(state));
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/quarantined")
    public ResponseEntity<List<String>> getQuarantinedTestNames(@RequestParam UUID teamId)
    {
        return ResponseEntity.ok(testService.getQuarantinedTestNames(teamId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TestIdentityResponse> getTestById(@PathVariable UUID id)
    {
        return ResponseEntity.ok(testService.getTestById(id));
    }
    @GetMapping("/{id}/runs")
    public ResponseEntity<List<TestRunResponse>>getRunsForTest(@PathVariable UUID id)
    {
        return ResponseEntity.ok(testService.getRunsForTest(id));
    }
    @GetMapping("/{id}/transitions")
    public ResponseEntity<List<StateTransitionResponse>> getTransitions(
            @PathVariable UUID id) {
        return ResponseEntity.ok(testService.getTransitionsForTest(id));
    }

    @PostMapping("/{id}/quarantine")
    public ResponseEntity<TestIdentityResponse> manualQuarantine(
            @PathVariable UUID id) {
        return ResponseEntity.ok(testService.manualQuarantine(id));
    }

    @PostMapping("/{id}/approve-recovery")
    public ResponseEntity<TestIdentityResponse> approveRecovery(
            @PathVariable UUID id) {
        return ResponseEntity.ok(testService.approveRecovery(id));
    }
}
