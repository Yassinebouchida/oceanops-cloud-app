package com.oceanopscloud.service;

import com.oceanopscloud.model.OrderComment;
import com.oceanopscloud.model.User;
import com.oceanopscloud.repository.OrderCommentRepository;
import com.oceanopscloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCommentService {

    private final OrderCommentRepository commentRepository;
    private final UserRepository userRepository;

    public OrderComment addComment(OrderComment comment) {
        return commentRepository.save(comment);
    }

    public List<OrderComment> getComments(Long orderId) {
        List<OrderComment> comments = commentRepository.findByOrderId(orderId);

        for (OrderComment c : comments) {
            // Anomaly system message should always appear as Agent
            if (c.getComment() != null && c.getComment().startsWith("ANOMALY:")) {
                c.setUsername("Agent Portuaire");
                c.setUserRole("AGENT");
                continue;
            }

            userRepository.findById(c.getUserId()).ifPresentOrElse(
                    user -> {
                        if ("ADMIN".equals(user.getRole().name())) {
                            c.setUsername("Shipchandler");
                        } else {
                            c.setUsername(user.getFullName());
                        }
                        c.setUserRole(user.getRole().name());
                    },
                    () -> {
                        c.setUsername("Utilisateur inconnu");
                        c.setUserRole("UNKNOWN");
                    }
            );
        }

        return comments;
    }
}