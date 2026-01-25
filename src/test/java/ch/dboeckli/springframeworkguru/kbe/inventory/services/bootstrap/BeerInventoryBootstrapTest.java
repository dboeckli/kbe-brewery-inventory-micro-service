package ch.dboeckli.springframeworkguru.kbe.inventory.services.bootstrap;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeerInventoryBootstrapTest {

    @Mock
    BeerInventoryRepository beerInventoryRepository;

    @InjectMocks
    BeerInventoryBootstrap bootstrap;

    @Test
    void runLoadsInitialInventoryWhenRepositoryIsEmpty() {
        when(beerInventoryRepository.count()).thenReturn(0L, 3L);
        when(beerInventoryRepository.findAll()).thenReturn(List.of());

        bootstrap.run();

        ArgumentCaptor<BeerInventory> captor = ArgumentCaptor.forClass(BeerInventory.class);
        verify(beerInventoryRepository, times(3)).save(captor.capture());

        List<String> upcs = captor.getAllValues().stream().map(BeerInventory::getUpc).toList();
        List<Integer> quantities = captor.getAllValues().stream().map(BeerInventory::getQuantityOnHand).toList();

        assertIterableEquals(List.of("0631234200036", "0631234300019", "0083783375213"), upcs);
        assertIterableEquals(List.of(10, 10, 50), quantities);
        verify(beerInventoryRepository).findAll();
    }

    @Test
    void runDoesNotLoadInventoryWhenRepositoryHasData() {
        when(beerInventoryRepository.count()).thenReturn(1L);

        bootstrap.run();

        verify(beerInventoryRepository).count();
        verifyNoMoreInteractions(beerInventoryRepository);
    }
}
