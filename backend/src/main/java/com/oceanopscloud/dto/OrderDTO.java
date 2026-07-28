package com.oceanopscloud.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderDTO {

    private Long id;

    private Long clientId;
    private String clientName;

    private String shipId;
    private String shipName;

    private String port;

    private Long agentId;
    private String agentName;

    private String description;

    private String status;

    private LocalDateTime createdAt;

    private boolean hasAttachment;

    private int commentCount;
    private String anomalyDescription; // New field for anomaly details
}
