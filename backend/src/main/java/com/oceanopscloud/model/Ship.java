package com.oceanopscloud.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imo;


    private int capacity;

    private String flag;

    private Long clientId;
}
