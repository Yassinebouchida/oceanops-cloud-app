package com.oceanopscloud.repository;

import com.oceanopscloud.model.OrderComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderCommentRepository extends JpaRepository<OrderComment, Long> {

    List<OrderComment> findByOrderId(Long orderId);
}
