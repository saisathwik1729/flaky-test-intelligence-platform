package com.ftip.ftip.repository;
import com.ftip.ftip.entity.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository

public interface TestRunRepository extends JpaRepository<TestRun, UUID> {
    List<TestRun>findByTestIdentityIdOrderByRunAtDesc(UUID testIdentityId);
    List<TestRun>findByTestIdentityIdAndRunAtAfterOrderByRunAtDesc(UUID testIdentityId, LocalDateTime after);
    @Query(value="SELECT result FROM test_run WHERE test_identity_id = :testId ORDER BY run_at DESC LIMIT :limit", nativeQuery=true)
    List<Object[]>findRecentResultsForTest(@Param("testId")UUID testId, @Param("limit") int limit);
}
