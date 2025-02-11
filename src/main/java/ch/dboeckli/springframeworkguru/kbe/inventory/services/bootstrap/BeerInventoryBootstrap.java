package ch.dboeckli.springframeworkguru.kbe.inventory.services.bootstrap;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerInventoryBootstrap implements CommandLineRunner {
    private static final String BEER_1_UPC = "0631234200036";
    private static final String BEER_2_UPC = "0631234300019";
    private static final String BEER_3_UPC = "0083783375213";

    private final BeerInventoryRepository beerInventoryRepository;

    @Override
    public void run(String... args) {
        if(beerInventoryRepository.count() == 0){
            loadInitialInv();
        }
    }

    private void loadInitialInv() {
        log.info("Loading initial beer inventory...");
        beerInventoryRepository.save(BeerInventory
                .builder()
                .upc(BEER_1_UPC)
                .quantityOnHand(10)
                .build());

        beerInventoryRepository.save(BeerInventory
                .builder()
                .upc(BEER_2_UPC)
                .quantityOnHand(10)
                .build());

        beerInventoryRepository.save(BeerInventory
                .builder()
                .upc(BEER_3_UPC)
                .quantityOnHand(50)
                .build());

        log.info("Loaded Inventory. Record count: " + beerInventoryRepository.count());
    }
}
