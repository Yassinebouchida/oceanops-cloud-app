package com.oceanopscloud.repository;

import com.oceanopscloud.model.AgentPortuaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgentPortuaireRepository extends JpaRepository<AgentPortuaire, Long> {

    List<AgentPortuaire> findByPortIgnoreCase(String port);
}
