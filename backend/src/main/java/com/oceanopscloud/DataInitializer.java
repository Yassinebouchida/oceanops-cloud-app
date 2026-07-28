package com.oceanopscloud;

import com.oceanopscloud.enums.UserRole;
import com.oceanopscloud.model.InventoryItem;
import com.oceanopscloud.model.User;
import com.oceanopscloud.repository.AgentPortuaireRepository;
import com.oceanopscloud.repository.InventoryRepository;
import com.oceanopscloud.repository.OrderRepository;
import com.oceanopscloud.repository.ShipRequestRepository;
import com.oceanopscloud.repository.UserRepository;
import com.oceanopscloud.service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @PostConstruct
    public void init() {
        System.out.println("🔥🔥🔥 DataInitializer BEAN CREATED 🔥🔥🔥");
    }

    private final UserRepository userRepository;
    private final ShipRequestRepository shipRequestRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final AgentPortuaireRepository agentPortuaireRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀🚀🚀 DataInitializer STARTING... 🚀🚀🚀");

        try {
            seedUsers();
            seedAgents();
            seedShipsAndRequests();
            seedInventory();
        } catch (Exception e) {
            System.err.println("❌❌❌ Error in DataInitializer: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✅✅✅ DataInitializer FINISHED... ✅✅✅");
    }

    private void seedInventory() {
        // Clear existing items to ensure fresh data with prices
        inventoryRepository.deleteAll();

        inventoryRepository.save(InventoryItem.builder().name("Fresh Water").category("Provisions").quantity(5000)
                .unit("Liters").price(1.0).build());
        inventoryRepository.save(InventoryItem.builder().name("Diesel Fuel").category("Fuel").quantity(12000)
                .unit("Liters").price(1.45).build());
        inventoryRepository.save(InventoryItem.builder().name("Mooring Ropes").category("Spare Parts").quantity(25)
                .unit("Units").price(250.0).build());
        inventoryRepository.save(InventoryItem.builder().name("Canned Food").category("Provisions").quantity(150)
                .unit("Boxes").price(65.00).build());
        inventoryRepository.save(
                InventoryItem.builder().name("Engine Oil").category("Fuel").quantity(400).unit("Liters")
                        .price(18.50).build());
        inventoryRepository.save(
                InventoryItem.builder().name("Safety Vests").category("Safety").quantity(50).unit("Units")
                        .price(45.0).build());
        System.out.println("✅ Generated Inventory Items with Prices CHECKED");
    }

    private void seedUsers() {
        // Create ADMIN if not exists
        if (userRepository.findByEmail("admin@oceanops.com").isEmpty()) {
            User admin = User.builder()
                    .fullName("Admin User")
                    .email("admin@oceanops.com")
                    .password("password") // Use plain text as per current configuration
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Generated Admin User: admin@oceanops.com / password");
        }

        // Create CLIENT (Captain) if not exists
        if (userRepository.findByEmail("khulud@oceanops.com").isEmpty()) {
            User captain = User.builder()
                    .fullName("Khulud")
                    .email("khulud@oceanops.com")
                    .password("password")
                    .role(UserRole.CLIENT)
                    .build();
            userRepository.save(captain);
            System.out.println("✅ Generated Client User: khulud@oceanops.com / password");
        }

        // Create AGENT if not exists
        if (userRepository.findByEmail("agent@oceanops.com").isEmpty()) {
            User agent = User.builder()
                    .fullName("Port Agent")
                    .email("agent@oceanops.com")
                    .password("password")
                    .role(UserRole.AGENT)
                    .agentPortuaireId(1L)
                    .build();
            userRepository.save(agent);
            System.out.println("✅ Generated Agent User: agent@oceanops.com / password");
        }
    }

    private void seedAgents() {
        if (agentPortuaireRepository.count() == 0) {
            agentPortuaireRepository
                    .save(com.oceanopscloud.model.AgentPortuaire.builder().companyName("Casaport Shipping Agency")
                            .contact("contact@casaport.ma").port("Casablanca").build());
            agentPortuaireRepository
                    .save(com.oceanopscloud.model.AgentPortuaire.builder().companyName("Atlas Port Services")
                            .contact("info@atlasport.ma").port("Casablanca").build());
            agentPortuaireRepository
                    .save(com.oceanopscloud.model.AgentPortuaire.builder().companyName("Maghreb Marine Agency")
                            .contact("support@maghrebmarine.ma").port("Casablanca").build());
            System.out.println("✅ Generated Port Agents");
        }
    }

    private void seedShipsAndRequests() {
        if (shipRequestRepository.count() == 0) {
            User client = userRepository.findByEmail("khulud@oceanops.com").orElse(null);

            if (client != null) {
                com.oceanopscloud.model.ShipRequest req = new com.oceanopscloud.model.ShipRequest();
                req.setClientId(client.getId());
                req.setClientName(client.getFullName());
                req.setShipName("Evergreen Given");
                req.setShipId("IMO 9811000");
                req.setPort("Casablanca");
                req.setUrgencyLevel("NORMAL");
                req.setStatus(com.oceanopscloud.enums.ShipRequestStatus.CREATED);
                req.setNotes("Please provide fresh water and provisions.");

                shipRequestRepository.save(req);
                System.out.println("✅ Generated Sample Ship Request");
            }
        }
    }
}
