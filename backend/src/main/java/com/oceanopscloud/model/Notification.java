package com.oceanopscloud.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;        // who receives the notification
    private Long orderId;       // related order (optional)

    private String message;     // text shown in the UI

    @Column(name = "is_read")
    private boolean read;

    private LocalDateTime createdAt = LocalDateTime.now();
}
