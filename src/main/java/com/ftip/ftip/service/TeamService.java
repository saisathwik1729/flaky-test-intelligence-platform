package com.ftip.ftip.service;
import com.ftip.ftip.dto.CreateTeamRequest;
import com.ftip.ftip.dto.TeamResponse;
import com.ftip.ftip.entity.Team;
import com.ftip.ftip.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    public TeamResponse createTeam(CreateTeamRequest request)
    {
        Team team=new Team();
        team.setName(request.getName());
        team.setRepoUrl(request.getRepoUrl());
        team.setFlakinessThreshold(request.getFlakinessThreshold());
        team.setAutoQuarantineThreshold(request.getAutoQuarantineThreshold());
        team.setRecoveryStreakRequired(request.getRecoveryStreakRequired());
        team.setScoringWindowDays(request.getScoringWindowDays());
        Team saved=teamRepository.save(team);
        return toResponse(saved);
    }
    public List<TeamResponse>getAllTeams()
    {
        return teamRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }
    public TeamResponse getTeamById(UUID id)
    {
        Team team=teamRepository.findById(id).orElseThrow(()->new RuntimeException("Team Not Found "+ id));
        return toResponse(team);
    }
    private TeamResponse toResponse(Team team)
    {
        TeamResponse response=new TeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setRepoUrl(team.getRepoUrl());
        response.setFlakinessThreshold(team.getFlakinessThreshold());
        response.setAutoQuarantineThreshold(team.getAutoQuarantineThreshold());
        response.setRecoveryStreakRequired(team.getRecoveryStreakRequired());
        response.setScoringWindowDays(team.getScoringWindowDays());
        return response;
    }
}
