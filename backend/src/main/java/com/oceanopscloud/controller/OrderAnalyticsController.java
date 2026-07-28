package com.oceanopscloud.controller;

import com.oceanopscloud.enums.OrderStatus;
import com.oceanopscloud.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class OrderAnalyticsController {

    private final OrderRepository orderRepository;

    // Total number of orders
    @GetMapping("/total-orders")
    public ResponseEntity<Long> getTotalOrders() {
        return ResponseEntity.ok(orderRepository.count());
    }

    // Orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<Long> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderRepository.countByStatus(status));
    }

    // Total anomalies
    @GetMapping("/anomalies")
    public ResponseEntity<Long> getAnomalies() {
        return ResponseEntity.ok(orderRepository.countByStatus(OrderStatus.ANOMALY_REPORTED));
    }

    // Orders for a specific port
    @GetMapping("/port/{port}")
    public ResponseEntity<Long> getOrdersByPort(@PathVariable String port) {
        return ResponseEntity.ok(orderRepository.countByPort(port));
    }

    // Orders handled by a specific agent
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<Long> getOrdersByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(orderRepository.countByAgentId(agentId));
    }
}
