package com.ftip.ftip.controller;
import com.ftip.ftip.dto.CreateTeamRequest;
import com.ftip.ftip.dto.TeamResponse;
import com.ftip.ftip.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    @PostMapping
    public ResponseEntity<TeamResponse>createTeam(@Valid @RequestBody CreateTeamRequest request){
        return ResponseEntity.ok(teamService.createTeam(request));
    }
    @GetMapping
    public ResponseEntity<List<TeamResponse>>getAllTeams(){
        return ResponseEntity.ok(teamService.getAllTeams());
    }
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable UUID id){
        return ResponseEntity.ok(teamService.getTeamById(id));
    }
}
