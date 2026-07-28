package com.oceanopscloud.service;

import com.oceanopscloud.dto.DashboardSummaryDTO;
import com.oceanopscloud.repository.OrderRepository;
import com.oceanopscloud.repository.ShipRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ShipRequestRepository shipRequestRepository;

    public DashboardSummaryDTO getSummary() {

        long totalOrders = orderRepository.count();
        long pendingRequests = shipRequestRepository.count(); // for now

        return new DashboardSummaryDTO(
                totalOrders,
                pendingRequests,
                0 // inventory is static for now
        );
    }
}
