package com.oceanopscloud.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ShipRequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipRequestId;

    private String oldStatus;
    private String newStatus;

    private LocalDateTime timestamp = LocalDateTime.now();
}
