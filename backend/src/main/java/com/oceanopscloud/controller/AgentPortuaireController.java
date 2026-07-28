package com.oceanopscloud.controller;

import com.oceanopscloud.model.AgentPortuaire;
import com.oceanopscloud.service.AgentPortuaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
public class AgentPortuaireController {

    private final AgentPortuaireService agentService;

    // GET /agents/port/Casablanca
    @GetMapping("/port/{port}")
    public ResponseEntity<List<AgentPortuaire>> getAgentsByPort(@PathVariable String port) {
        return ResponseEntity.ok(agentService.getAgentsByPort(port));
    }

    // GET /agents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AgentPortuaire> getAgentById(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.getAgentById(id));
    }
}
