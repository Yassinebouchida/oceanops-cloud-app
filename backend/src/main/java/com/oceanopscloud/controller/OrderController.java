package com.oceanopscloud.controller;

import com.oceanopscloud.dto.OrderDTO;
import com.oceanopscloud.enums.OrderStatus;
import com.oceanopscloud.model.Order;
import com.oceanopscloud.model.OrderStatusHistory;
import com.oceanopscloud.service.FileService;
import com.oceanopscloud.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import java.io.File;
import java.io.FileInputStream;
import com.oceanopscloud.service.OrderCommentService;
import com.oceanopscloud.model.OrderComment;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final FileService fileService;
    private final OrderCommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    // Orders by client
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Order>> getClientOrders(@PathVariable Long clientId) {
        return ResponseEntity.ok(orderService.getOrdersForClient(clientId));
    }

    // Orders by agent
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Order>> getAgentOrders(@PathVariable Long agentId) {
        return ResponseEntity.ok(orderService.getOrdersForAgent(agentId));
    }

    // Orders by ship
    @GetMapping("/ship/{shipId}")
    public ResponseEntity<List<Order>> getShipOrders(@PathVariable String shipId) {
        return ResponseEntity.ok(orderService.getOrdersForShip(shipId));
    }

    // Get one order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderService.convertToDTO(order));
    }

    // Update status
    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long orderId,
            @PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    // Get history
    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderStatusHistory>> getHistory(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderHistory(orderId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> dtos = orderService.getAllOrders()
                .stream()
                .map(orderService::convertToDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(
            @RequestParam(required = false) String port,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long agentId) {
        return ResponseEntity.ok(orderService.searchOrders(port, status, agentId));
    }

    // Delete order
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted");
    }

    @PostMapping("/{orderId}/upload")
    public ResponseEntity<String> uploadAttachment(
            @PathVariable Long orderId,
            @RequestParam("file") MultipartFile file) {
        try {
            // 1. Check order exists
            Order order = orderService.getOrderById(orderId);

            // 2. Save file to disk
            String filePath = fileService.saveFile(file);

            // 3. Attach file to order
            order.setAttachments(filePath);
            orderService.save(order);

            return ResponseEntity.ok("File uploaded: " + filePath);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}/attachment")
    public ResponseEntity<?> downloadAttachment(@PathVariable Long orderId) {
        try {
            // 1. Check order exists
            Order order = orderService.getOrderById(orderId);

            // 2. Check file exists
            if (order.getAttachments() == null) {
                return ResponseEntity.badRequest().body("No attachment found for this order");
            }

            // 3. Load the file from disk
            File file = new File(order.getAttachments());

            if (!file.exists()) {
                return ResponseEntity.badRequest().body("File not found on server");
            }

            // 4. Return file as stream
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + file.getName())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/comments")
    public ResponseEntity<OrderComment> addComment(
            @PathVariable Long orderId,
            @RequestParam Long userId,
            @RequestParam String comment) {
        OrderComment c = new OrderComment();
        c.setOrderId(orderId);
        c.setUserId(userId);
        c.setComment(comment);

        return ResponseEntity.ok(commentService.addComment(c));
    }

    @PostMapping("/{orderId}/anomaly")
    public ResponseEntity<String> reportAnomaly(
            @PathVariable Long orderId,
            @RequestBody String comment) {
        orderService.reportAnomaly(orderId, comment);
        return ResponseEntity.ok("Anomaly reported.");
    }

    @GetMapping("/{orderId}/comments")
    public ResponseEntity<List<OrderComment>> getComments(@PathVariable Long orderId) {
        return ResponseEntity.ok(commentService.getComments(orderId));
    }
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders() {
        List<OrderDTO> dtos = orderService.getAllOrders()
                .stream()
                .map(orderService::convertToDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
