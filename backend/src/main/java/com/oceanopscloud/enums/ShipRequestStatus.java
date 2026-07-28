package com.oceanopscloud.enums;

public enum ShipRequestStatus {
    CREATED,
    APPROVED,   // <-- ADD THIS
    VALIDATED,
    IN_PREPARATION,
    READY_FOR_DELIVERY,
    COMPLETED,
    REJECTED
}
