package com.oceanopscloud.repository;

import com.oceanopscloud.model.ShipRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipRequestHistoryRepository extends JpaRepository<ShipRequestHistory, Long> {
}
