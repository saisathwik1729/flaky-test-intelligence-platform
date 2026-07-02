package com.ftip.ftip.repository;
import com.ftip.ftip.entity.StateTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository

public interface StateTransitionLogRepository extends JpaRepository<StateTransitionLog, UUID> {
    List<StateTransitionLog>findByTestIdentityIdOrderByTransitionedAtDesc(UUID testIdentityId);

}
