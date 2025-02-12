package ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by jt on 2019-05-31.
 */
public interface BeerInventoryRepository extends JpaRepository<BeerInventory, UUID>, PagingAndSortingRepository<BeerInventory, UUID> {

    List<BeerInventory> findAllByBeerId(String beerId);

    List<BeerInventory> findAllByUpc(String upc);

}
