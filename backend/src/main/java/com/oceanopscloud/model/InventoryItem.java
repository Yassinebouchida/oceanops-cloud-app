package com.oceanopscloud.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category; // e.g., "Provisions", "Spare Parts", "Fuel"
    private int quantity;
    private String unit; // e.g., "kg", "liters", "units"
    private Double price; // Price per unit
}
