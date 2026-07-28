package com.oceanopscloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummaryDTO {
    private long totalOrders;
    private long pendingRequests;
    private long inventoryItems;
}
