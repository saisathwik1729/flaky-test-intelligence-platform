package com.ftip.ftip.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ftip.ftip.entity.NotificationLog;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    List<NotificationLog> findByTestIdentityIdOrderBySentAtDesc(UUID testIdentityId);
}