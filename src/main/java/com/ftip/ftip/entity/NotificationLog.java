package com.ftip.ftip.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="notification_log")
@Data

public class NotificationLog {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="test_identity_id",nullable=false)
    private TestIdentity testIdentity;

    private String type;
    private String channel;
    private String recipients;

    @Column(columnDefinition="TEXT")
    private String message;

    private String status;

    @Column(name="sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    public void prePersisit()
    {
        this.sentAt = LocalDateTime.now();
    }
}
