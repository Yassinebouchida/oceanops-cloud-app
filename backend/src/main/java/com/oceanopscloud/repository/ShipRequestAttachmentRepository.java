package com.oceanopscloud.repository;

import com.oceanopscloud.model.ShipRequestAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipRequestAttachmentRepository
        extends JpaRepository<ShipRequestAttachment, Long> {
}