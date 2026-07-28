package com.oceanopscloud.repository;

import com.oceanopscloud.model.ShipRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipRequestRepository extends JpaRepository<ShipRequest, Long> {
}
