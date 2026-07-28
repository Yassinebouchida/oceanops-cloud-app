package com.oceanopscloud.service;

import com.oceanopscloud.enums.OrderStatus;
import com.oceanopscloud.enums.ShipRequestStatus;
import com.oceanopscloud.model.*;
import com.oceanopscloud.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ShipRequestService {

    private final ShipRequestRepository shipRequestRepository;
    private final ShipRequestHistoryRepository historyRepository;
    private final OrderRepository orderRepository;
    private final ShipRequestItemRepository shipRequestItemRepository;
    private final ShipRequestAttachmentRepository attachmentRepository;
    private final AiService aiService;

    // 1. Create Ship Request
    public ShipRequest create(ShipRequest request) {

        // 🔥 FIX: Use authenticated user instead of request body text
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof com.oceanopscloud.model.User) {
            com.oceanopscloud.model.User user = (com.oceanopscloud.model.User) principal;
            request.setClientId(user.getId());
            request.setClientName(user.getFullName()); // This forces the correct name!
        }

        request.setStatus(ShipRequestStatus.CREATED);

        ShipRequest savedRequest = shipRequestRepository.save(request);

        if (request.getItems() != null) {
            for (ShipRequestItem item : request.getItems()) {
                item.setShipRequest(savedRequest);
            }
            shipRequestItemRepository.saveAll(request.getItems());
        }

        return savedRequest;
    }

    // 2. Get all
    public List<ShipRequest> getAll() {
        return shipRequestRepository.findAll();
    }

    // 3. Update status
    public ShipRequest updateStatus(Long id, ShipRequestStatus newStatus) {
        ShipRequest req = shipRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        ShipRequestStatus old = req.getStatus();
        req.setStatus(newStatus);

        ShipRequestHistory h = new ShipRequestHistory();
        h.setShipRequestId(id);
        h.setOldStatus(old.name());
        h.setNewStatus(newStatus.name());
        historyRepository.save(h);

        if (newStatus == ShipRequestStatus.APPROVED && req.getAgentId() == null) {
            throw new RuntimeException("Agent must be selected before approval");
        }

        if (newStatus == ShipRequestStatus.APPROVED) {
            convertToOrder(req);
        }

        return shipRequestRepository.save(req);
    }

    // 4. Convert to Order
    private void convertToOrder(ShipRequest req) {
        Order order = new Order();
        order.setClientId(req.getClientId());
        order.setShipId(req.getShipId());
        order.setPort(req.getPort());
        order.setAgentId(req.getAgentId()); // ✅ FROM REQUEST
        order.setDescription("Supply Request - " + req.getShipName());
        order.setStatus(OrderStatus.CREATED);
        order.setShipName(req.getShipName());

        orderRepository.save(order);
    }

    // 5. AI Analysis (FINAL METHOD)
    public HashMap<String, Object> aiAnalyzeRequest(Long id) {
        ShipRequest req = getById(id);
        return aiService.analyzeRequest(req); // CALL AI SERVICE
    }

    public ShipRequest getById(Long id) {
        return shipRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ship request not found"));
    }

    public ShipRequest save(ShipRequest req) {
        return shipRequestRepository.save(req);
    }

    public ShipRequestAttachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
    }
}
