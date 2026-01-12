package ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface BeerInventoryRepository extends JpaRepository<BeerInventory, UUID>, PagingAndSortingRepository<BeerInventory, UUID> {

    List<BeerInventory> findAllByBeerId(String beerId);

    List<BeerInventory> findAllById(UUID id);

    List<BeerInventory> findAllByUpc(String upc);

    List<BeerInventory> findAll();

}
