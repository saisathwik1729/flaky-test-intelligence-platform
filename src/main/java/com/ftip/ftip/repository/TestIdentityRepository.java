package com.ftip.ftip.repository;
import com.ftip.ftip.entity.TestIdentity;
import com.ftip.ftip.entity.TestState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface TestIdentityRepository extends JpaRepository<TestIdentity, UUID> {
    List<TestIdentity>findByTeamId(UUID teamId);
    List<TestIdentity>findByCurrentState(TestState state);
    List<TestIdentity>findByTeamIdAndCurrentState(UUID teamId, TestState state);
    Optional<TestIdentity> findByTeamIdAndTestName(UUID teamId, String testName);
}
