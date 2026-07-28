package com.oceanopscloud.service;

import com.oceanopscloud.dto.OrderDTO;
import com.oceanopscloud.enums.OrderStatus;
import com.oceanopscloud.model.Order;
import com.oceanopscloud.model.OrderComment;
import com.oceanopscloud.model.OrderStatusHistory;
import com.oceanopscloud.model.User;
import com.oceanopscloud.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.oceanopscloud.model.AgentPortuaire;
@Service
@RequiredArgsConstructor
public class OrderService {
    private final AgentPortuaireRepository agentPortuaireRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final NotificationService notificationService;

    private final UserRepository userRepository;
    private final ShipRepository shipRepository;
    private final OrderCommentRepository orderCommentRepository;

    // --------------------------------------------------------------------
    // ORDER CRUD + WORKFLOW
    // --------------------------------------------------------------------

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersForClient(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public List<Order> getOrdersForAgent(Long agentId) {
        return orderRepository.findByAgentId(agentId);
    }

    public List<Order> getOrdersForShip(String shipId) {
        return orderRepository.findByShipId(shipId);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();

        if (!isValidTransition(oldStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition: " + oldStatus + " → " + newStatus);
        }

        // Save history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(orderId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        historyRepository.save(history);

        // Update
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        String msg = "Order #" + orderId + " status changed from " + oldStatus + " ➜ " + newStatus;
        sendNotifications(order, msg, newStatus);

        return savedOrder;
    }

    private void sendNotifications(Order order, String message, OrderStatus newStatus) {

        Long clientId = order.getClientId();
        Long agentId = order.getAgentId();
        Long shipchandlerId = 1L; // Admin ID

        notificationService.send(clientId, order.getId(), message);
        notificationService.send(agentId, order.getId(), message);
        notificationService.send(shipchandlerId, order.getId(), message);

        if (newStatus == OrderStatus.ANOMALY_REPORTED) {
            String anomalyMsg = "⚠️ Anomaly reported for Order #" + order.getId();
            notificationService.send(clientId, order.getId(), anomalyMsg);
            notificationService.send(agentId, order.getId(), anomalyMsg);
            notificationService.send(shipchandlerId, order.getId(), anomalyMsg);
        }
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {

        // Agent can report anomaly only during delivery
        if (next == OrderStatus.ANOMALY_REPORTED) {
            return current == OrderStatus.IN_DELIVERY;
        }

        switch (current) {

            case CREATED:
                return next == OrderStatus.VALIDATED;

            case VALIDATED:
                return next == OrderStatus.IN_PREPARATION;

            case IN_PREPARATION:
                return next == OrderStatus.IN_DELIVERY;

            case IN_DELIVERY:
                return next == OrderStatus.DELIVERED
                        || next == OrderStatus.ANOMALY_REPORTED;

            case ANOMALY_REPORTED:
                return next == OrderStatus.ANOMALY_RESOLVED;

            case ANOMALY_RESOLVED:
                return next == OrderStatus.DELIVERED;

            case DELIVERED:
                return next == OrderStatus.SUPERVISED;

            case SUPERVISED:
                return false;

            default:
                return false;
        }
    }

    public List<OrderStatusHistory> getOrderHistory(Long orderId) {
        return historyRepository.findByOrderId(orderId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> searchOrders(String port, OrderStatus status, Long agentId) {
        return orderRepository.searchOrders(port, status, agentId);
    }

    // --------------------------------------------------------------------
    // DTO CONVERTER (FINAL VERSION)
    // --------------------------------------------------------------------

    public void reportAnomaly(Long orderId, String comment) {
        Order order = getOrderById(orderId);

        // 1. Add comment
        OrderComment c = new OrderComment();
        c.setOrderId(orderId);
        c.setUserId(order.getAgentId()); // Agent is reporting
        c.setComment("ANOMALY: " + comment);
        orderCommentRepository.save(c);

        // 2. Update Status
        updateOrderStatus(orderId, OrderStatus.ANOMALY_REPORTED);

        // 3. Send detailed notification (updateOrderStatus sends generic one, we send
        // specific one)
        // actually updateOrderStatus sends "Anomaly reported" generic message.
        // We might want to send the specific comment too.
        String detailedMsg = "⚠️ Anomaly Detail for Order #" + orderId + ": " + comment;
        notificationService.send(order.getClientId(), orderId, detailedMsg);
        notificationService.send(1L, orderId, detailedMsg); // Admin
    }

    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();

        dto.setId(order.getId());
        dto.setClientId(order.getClientId());
        dto.setShipId(order.getShipId());
        dto.setAgentId(order.getAgentId());
        dto.setPort(order.getPort());
        dto.setDescription(order.getDescription());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());

        // Attachments
        dto.setHasAttachment(order.getAttachments() != null);

        // Anomaly Description
        // Find latest comment starting with "ANOMALY:"
        List<com.oceanopscloud.model.OrderComment> comments = orderCommentRepository.findByOrderId(order.getId());
        dto.setCommentCount(comments.size());

        dto.setAnomalyDescription(
                comments.stream()
                        .filter(c -> c.getComment().startsWith("ANOMALY:"))
                        .reduce((first, second) -> second) // Get last one
                        .map(c -> c.getComment().replace("ANOMALY: ", ""))
                        .orElse(null));

        // Client name
        dto.setClientName(
                userRepository.findById(order.getClientId())
                        .map(User::getFullName)
                        .orElse("Unknown Client"));

        // Agent name
        dto.setAgentName(
                agentPortuaireRepository.findById(order.getAgentId())
                        .map(AgentPortuaire::getCompanyName)
                        .orElse("Unknown Agent"));

       // Ship name
        dto.setShipName(
                order.getShipName() != null ? order.getShipName() : order.getShipId()
        );

        return dto;
    }
}
