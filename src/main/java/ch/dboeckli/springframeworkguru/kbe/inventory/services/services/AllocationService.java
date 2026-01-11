package ch.dboeckli.springframeworkguru.kbe.inventory.services.services;


import ch.guru.springframework.kbe.lib.dto.BeerOrderDto;

public interface AllocationService {

    Boolean allocateOrder(BeerOrderDto beerOrderDto);
}
