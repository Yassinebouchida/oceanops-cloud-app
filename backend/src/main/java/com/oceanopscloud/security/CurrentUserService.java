package com.oceanopscloud.security;

import com.oceanopscloud.model.User;
import com.oceanopscloud.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final HttpServletRequest request;

    public User getCurrentUser() {
        String userIdHeader = request.getHeader("X-USER-ID");

        if (userIdHeader == null) {
            throw new RuntimeException("Missing X-USER-ID header");
        }

        Long userId = Long.parseLong(userIdHeader);

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
