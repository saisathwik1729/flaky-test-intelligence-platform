package com.ftip.ftip.controller;
import com.ftip.ftip.dto.FlakinessSummaryResponse;
import com.ftip.ftip.dto.TestIdentityResponse;
import com.ftip.ftip.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    @GetMapping("/summary")
    public ResponseEntity<FlakinessSummaryResponse> getSummary(@RequestParam UUID teamId){
        return ResponseEntity.ok(reportService.getTeamSummary(teamId));
    }
    @GetMapping("/flaky-leaderboard")
    public ResponseEntity<List<TestIdentityResponse>>getLeaderboard(@RequestParam UUID teamId){
        return ResponseEntity.ok(reportService.getFlakyLeaderboard(teamId));
    }
}
