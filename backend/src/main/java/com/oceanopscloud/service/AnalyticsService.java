package com.oceanopscloud.service;

import com.oceanopscloud.model.ShipRequest;
import com.oceanopscloud.repository.ShipRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ShipRequestRepository shipRequestRepository;

    public Map<String, Long> countRequestsByStatus() {
        Map<String, Long> data = new HashMap<>();

        shipRequestRepository.findAll().forEach(req -> {
            String status = req.getStatus().name(); // ✅ FIX
            data.merge(status, 1L, Long::sum);
        });

        return data;
    }
    public Map<String, Long> countRequestsByPort() {
        Map<String, Long> data = new HashMap<>();

        shipRequestRepository.findAll().forEach(req -> {
            String port = normalizePort(req.getPort());
            data.merge(port, 1L, Long::sum);
        });

        return data;
    }

    private String normalizePort(String port) {
        if (port == null) return "UNKNOWN";

        port = port.trim().toUpperCase();

        if (port.contains("CASABLANCA")) {
            return "CASABLANCA";
        }
        if (port.contains("JORF")) {
            return "JORF LASFAR";
        }

        return port;
    }

}
