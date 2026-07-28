package com.oceanopscloud.repository;

import com.oceanopscloud.model.Ship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipRepository extends JpaRepository<Ship, Long> {

    List<Ship> findByClientId(Long clientId);

    Optional<Ship> findByImo(String imo);

}
