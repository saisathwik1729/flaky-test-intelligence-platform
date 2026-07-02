package com.ftip.ftip.repository;
import com.ftip.ftip.entity.Quarantine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface QuarantineRepository extends JpaRepository<Quarantine, UUID> {
    Optional<Quarantine>findByTestIdentityId(UUID testIdentityId);
}
