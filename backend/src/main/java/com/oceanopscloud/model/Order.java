package com.oceanopscloud.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.oceanopscloud.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Ship ID is required")
    private String shipId;


    private String shipName;

    @NotBlank(message = "Port is required")
    private String port;

    @NotNull(message = "Agent ID is required")
    private Long agentId;

    @NotBlank(message = "Description is required")
    @Size(min = 5, message = "Description must be at least 5 characters")
    private String description;


    private String attachments;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;
    @Column(length = 2000)
    private String anomalyDescription;

    @Column(length = 2000)
    private String anomalyReply;

    private String anomalyRepliedBy;

    private LocalDateTime anomalyRepliedAt;

    private LocalDateTime createdAt = LocalDateTime.now();


}
