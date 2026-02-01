package ch.dboeckli.springframeworkguru.kbe.inventory.services.listeners;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import ch.guru.springframework.kbe.lib.dto.BeerDto;
import ch.guru.springframework.kbe.lib.events.NewInventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.docker.compose.skip.in-tests=false"
})
@Slf4j
class NewInventoryListenerIT {

    @Autowired
    NewInventoryListener newInventoryListener;

    @Autowired
    BeerInventoryRepository beerInventoryRepository;

    @Test
    void testNewInventoryListener() {
        beerInventoryRepository.deleteAll();
        // Arrange
        BeerDto beerDto = BeerDto.builder()
            .id(UUID.randomUUID())
            .quantityOnHand(1)
            .upc("UPC-PLACEHOLDER")
            .build();
        NewInventoryEvent event = new NewInventoryEvent(beerDto);

        // Act
        newInventoryListener.listen(event);

        // Assert
        List<BeerInventory> saved = beerInventoryRepository.findAllByUpc("UPC-PLACEHOLDER");
        assertEquals(1, saved.size());

        BeerInventory inventory = saved.getFirst();
        assertEquals(beerDto.getId().toString(), inventory.getBeerId());
        assertEquals(beerDto.getQuantityOnHand(), inventory.getQuantityOnHand());
        assertEquals(beerDto.getUpc(), inventory.getUpc());
    }


}