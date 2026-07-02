package com.ftip.ftip.controller;
import com.ftip.ftip.dto.StateTransitionResponse;
import com.ftip.ftip.dto.TestIdentityResponse;
import com.ftip.ftip.dto.TestRunResponse;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

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
