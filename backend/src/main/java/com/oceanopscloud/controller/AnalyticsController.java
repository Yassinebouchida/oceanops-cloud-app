package com.oceanopscloud.controller;

import com.oceanopscloud.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/requests-by-status")
    public Map<String, Long> requestsByStatus() {
        return analyticsService.countRequestsByStatus();
    }
    @GetMapping("/requests-by-port")
    public Map<String, Long> requestsByPort() {
        return analyticsService.countRequestsByPort();
    }

}
