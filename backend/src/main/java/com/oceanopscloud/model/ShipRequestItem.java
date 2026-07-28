package com.oceanopscloud.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class ShipRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private int quantity;
    private String category;
    private String unit;
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "ship_request_id")
    @JsonBackReference // Prevent circular JSON
    @ToString.Exclude
    private ShipRequest shipRequest;
}
