package com.oceanopscloud.repository;

import com.oceanopscloud.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.oceanopscloud.enums.OrderStatus;


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClientId(Long clientId);

    List<Order> findByAgentId(Long agentId);

    List<Order> findByShipId(String shipId);

    long countByStatus(OrderStatus status);

    long countByPort(String port);

    long countByAgentId(Long agentId);


    @Query("""
       SELECT o FROM Order o
       WHERE (:port IS NULL OR o.port = :port)
       AND (:status IS NULL OR o.status = :status)
       AND (:agentId IS NULL OR o.agentId = :agentId)
       """)
    List<Order> searchOrders(
            @Param("port") String port,
            @Param("status") OrderStatus status,
            @Param("agentId") Long agentId
    );

}


