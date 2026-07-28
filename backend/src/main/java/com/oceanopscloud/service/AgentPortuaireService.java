package com.oceanopscloud.service;

import com.oceanopscloud.model.AgentPortuaire;
import com.oceanopscloud.repository.AgentPortuaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentPortuaireService {

    private final AgentPortuaireRepository agentRepo;

    public List<AgentPortuaire> getAgentsByPort(String port) {
        return agentRepo.findByPortIgnoreCase(port);
    }



    public AgentPortuaire getAgentById(Long id) {
        return agentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
    }
}
