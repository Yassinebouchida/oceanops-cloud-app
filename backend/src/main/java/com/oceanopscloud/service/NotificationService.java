package com.oceanopscloud.service;

import com.oceanopscloud.model.Notification;
import com.oceanopscloud.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void send(Long userId, Long orderId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setOrderId(orderId);
        n.setMessage(message);
        n.setRead(false);
        notificationRepository.save(n);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }
}
