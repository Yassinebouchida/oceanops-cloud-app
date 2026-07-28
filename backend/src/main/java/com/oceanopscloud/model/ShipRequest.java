package com.oceanopscloud.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oceanopscloud.enums.ShipRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ship_request")
public class ShipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shipId;
    private String shipName;

    private Long clientId;
    private String clientName;

    private Long agentId; // ✅ SELECTED BY CLIENT

    private String port;
    private String urgencyLevel;

    private LocalDate eta;
    private LocalDate requestedDeliveryDate;

    @Lob
    private String notes;

    @Enumerated(EnumType.STRING)
    private ShipRequestStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "shipRequest", cascade = CascadeType.ALL)
    @JsonManagedReference //  Pair with @JsonBackReference in items
    @ToString.Exclude
    private List<ShipRequestItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "shipRequest", cascade = CascadeType.ALL)
    @JsonManagedReference //  Pair with @JsonBackReference in attachments
    @ToString.Exclude
    private List<ShipRequestAttachment> attachments = new ArrayList<>();
}
