package ch.dboeckli.springframeworkguru.kbe.inventory.services.web.controllers;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.web.mappers.BeerInventoryMapper;
import ch.guru.springframework.kbe.lib.dto.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerInventoryController {

    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerInventoryMapper beerInventoryMapper;

    @GetMapping("api/v1/beer/{beerId}/inventory")
    List<BeerInventoryDto> listBeersById(@PathVariable UUID beerId){
        log.info("Finding Inventory for beerId:" + beerId);

        List<BeerInventory> beers = beerInventoryRepository.findAllByBeerId(beerId.toString());

        return beerInventoryRepository.findAllByBeerId(beerId.toString())
                .stream()
                .map(beerInventoryMapper::beerInventoryToBeerInventoryDto)
                .toList();
    }

    @GetMapping("api/v1/beer/inventory")
    List<BeerInventoryDto> listAllBeers(){
        return beerInventoryRepository.findAll()
            .stream()
            .map(beerInventoryMapper::beerInventoryToBeerInventoryDto)
            .toList();
    }
}
