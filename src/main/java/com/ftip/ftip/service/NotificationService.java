package com.ftip.ftip.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ftip.ftip.entity.NotificationLog;
import com.ftip.ftip.entity.TestIdentity;
import com.ftip.ftip.entity.TestState;
import com.ftip.ftip.repository.NotificationLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;

    @Transactional
    public void notifyStateChange(TestIdentity testIdentity, TestState fromState, TestState toState)
    {
        NotificationLog notification=new NotificationLog();
        notification.setTestIdentity(testIdentity);
        notification.setType("STATE_CHANGE");
        notification.setChannel("EMAIL");
        notification.setRecipients(testIdentity.getOwnerEmail());
        notification.setMessage("Test "+testIdentity.getTestName()
                +" moved from "+fromState+" to "+toState
                +" (score "+testIdentity.getFlakinessScore()+")");
        notification.setStatus("PENDING");
        notification.setSentAt(LocalDateTime.now());
        notificationLogRepository.save(notification);

        log.info("Notification queued for {} -> {}", testIdentity.getTestName(), toState);
    }
}