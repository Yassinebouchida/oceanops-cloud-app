package com.oceanopscloud.model;

import jakarta.persistence.*;
import lombok.*;
import com.oceanopscloud.enums.UserRole;


import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "agent_portuaire_id")
    private Long agentPortuaireId;


}
