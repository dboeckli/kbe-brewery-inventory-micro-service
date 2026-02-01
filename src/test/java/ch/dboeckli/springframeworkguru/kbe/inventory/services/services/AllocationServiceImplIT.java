package ch.dboeckli.springframeworkguru.kbe.inventory.services.services;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import ch.guru.springframework.kbe.lib.dto.BeerOrderDto;
import ch.guru.springframework.kbe.lib.dto.BeerOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    properties = {
        "spring.docker.compose.skip.in-tests=false"
    }
)
@Slf4j
class AllocationServiceImplIT {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private BeerInventoryRepository beerInventoryRepository;

    @Test
    void allocateOrder() {
        String upc = "123456789";

        BeerInventory inventory = BeerInventory.builder()
            .upc(upc)
            .quantityOnHand(10)
            .build();

        beerInventoryRepository.save(inventory);

        BeerOrderLineDto orderLine = BeerOrderLineDto.builder()
            .id(UUID.randomUUID())
            .upc(upc)
            .orderQuantity(5)
            .quantityAllocated(0)
            .build();

        BeerOrderDto order = BeerOrderDto.builder()
            .id(UUID.randomUUID())
            .beerOrderLines(List.of(orderLine))
            .build();

        Boolean fullyAllocated = allocationService.allocateOrder(order);

        assertTrue(fullyAllocated, "Order should be fully allocated");
        assertEquals(5, orderLine.getQuantityAllocated());

        BeerInventory updatedInventory = beerInventoryRepository.findAllByUpc(upc).getFirst();
        assertEquals(5, updatedInventory.getQuantityOnHand());
    }

}