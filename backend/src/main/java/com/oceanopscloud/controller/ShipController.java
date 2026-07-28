package com.oceanopscloud.controller;

import com.oceanopscloud.model.Ship;
import com.oceanopscloud.service.ShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/ships")
@RequiredArgsConstructor
public class ShipController {

    private final ShipService shipService;

    @PostMapping("/create")
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        return ResponseEntity.ok(shipService.saveShip(ship));
    }

    @GetMapping("/my/{clientId}")
    public ResponseEntity<List<Ship>> getMyShips(@PathVariable Long clientId) {
        return ResponseEntity.ok(shipService.getShipsForClient(clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable Long id) {
        return ResponseEntity.ok(shipService.getShipById(id));
    }
}

