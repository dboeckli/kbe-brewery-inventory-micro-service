package ch.dboeckli.springframeworkguru.kbe.inventory.services.listeners;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.config.JmsConfig;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import ch.guru.springframework.kbe.lib.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by jt on 2019-05-31.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){
        log.info("Received NewInventoryEvent: {}", event.toString());

        beerInventoryRepository.save(BeerInventory.builder()
                .beerId(event.getBeerDto().getId().toString())
                .quantityOnHand(event.getBeerDto().getQuantityOnHand())
                .upc(event.getBeerDto().getUpc())
                .build());
    }
}
