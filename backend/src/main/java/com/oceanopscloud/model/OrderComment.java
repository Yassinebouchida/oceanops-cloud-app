package com.oceanopscloud.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "order_comments")
public class OrderComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long userId;

    @Column(nullable = false, length = 500)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient
    private String username;

    @Transient
    private String userRole;
}
