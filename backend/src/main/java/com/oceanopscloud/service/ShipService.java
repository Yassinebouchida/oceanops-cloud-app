package com.oceanopscloud.service;

import com.oceanopscloud.model.Ship;
import com.oceanopscloud.repository.ShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipService {

    private final ShipRepository shipRepository;

    public List<Ship> getShipsForClient(Long clientId) {
        return shipRepository.findByClientId(clientId);
    }


    public Ship getShipById(Long id) {
        return shipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ship not found"));
    }

    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }
}
