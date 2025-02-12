package ch.dboeckli.springframeworkguru.kbe.inventory.services.web.mappers;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.guru.springframework.kbe.lib.dto.BeerInventoryDto;
import org.mapstruct.Mapper;


@Mapper(uses = {DateMapper.class})
public interface BeerInventoryMapper {

    BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDto beerInventoryDTO);

    BeerInventoryDto beerInventoryToBeerInventoryDto(BeerInventory beerInventory);
}
