package ch.dboeckli.springframeworkguru.kbe.inventory.services.services;


import ch.dboeckli.springframeworkguru.kbe.inventory.services.dto.BeerOrderDto;

/**
 * Created by jt on 2019-09-09.
 */
public interface AllocationService {

    Boolean allocateOrder(BeerOrderDto beerOrderDto);
}
