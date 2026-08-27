package ch.dboeckli.springframeworkguru.kbe.inventory.services.services;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import ch.guru.springframework.kbe.lib.dto.BeerOrderDto;
import ch.guru.springframework.kbe.lib.dto.BeerOrderLineDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AllocationServiceImplTest {

    @Mock
    BeerInventoryRepository beerInventoryRepository;

    @InjectMocks
    AllocationServiceImpl allocationService;

    private BeerOrderDto orderWith(String upc, int orderQuantity, Integer quantityAllocated) {
        BeerOrderLineDto orderLine = BeerOrderLineDto.builder()
            .id(UUID.randomUUID())
            .upc(upc)
            .orderQuantity(orderQuantity)
            .quantityAllocated(quantityAllocated)
            .build();
        return BeerOrderDto.builder().id(UUID.randomUUID()).beerOrderLines(List.of(orderLine)).build();
    }

    @Test
    void allocateOrder_fullAllocation_returnsTrueAndSavesReducedInventory() {
        String upc = "0631234200036";
        BeerInventory inventory = BeerInventory.builder().upc(upc).quantityOnHand(10).build();
        given(beerInventoryRepository.findAllByUpc(upc)).willReturn(List.of(inventory));

        BeerOrderDto order = orderWith(upc, 5, 0);
        boolean result = allocationService.allocateOrder(order);

        assertTrue(result);
        assertEquals(5, order.getBeerOrderLines().getFirst().getQuantityAllocated());
        verify(beerInventoryRepository).save(inventory);
        assertEquals(5, inventory.getQuantityOnHand());
        verify(beerInventoryRepository, never()).delete(any(BeerInventory.class));
    }

    @Test
    void allocateOrder_partialAllocation_deletesInventoryAndReturnsFalse() {
        String upc = "0631234200036";
        BeerInventory inventory = BeerInventory.builder().upc(upc).quantityOnHand(3).build();
        given(beerInventoryRepository.findAllByUpc(upc)).willReturn(List.of(inventory));

        BeerOrderDto order = orderWith(upc, 5, 0);
        boolean result = allocationService.allocateOrder(order);

        assertFalse(result);
        assertEquals(3, order.getBeerOrderLines().getFirst().getQuantityAllocated());
        verify(beerInventoryRepository).delete(inventory);
        verify(beerInventoryRepository, never()).save(any(BeerInventory.class));
    }

    @Test
    void allocateOrder_noInventory_returnsFalseAndAllocatesNothing() {
        String upc = "0631234200036";
        given(beerInventoryRepository.findAllByUpc(upc)).willReturn(List.of());

        BeerOrderDto order = orderWith(upc, 5, 0);
        boolean result = allocationService.allocateOrder(order);

        assertFalse(result);
        assertEquals(0, order.getBeerOrderLines().getFirst().getQuantityAllocated());
        verify(beerInventoryRepository, never()).save(any(BeerInventory.class));
        verify(beerInventoryRepository, never()).delete(any(BeerInventory.class));
    }

    @Test
    void allocateOrder_zeroInventory_returnsFalseAndAllocatesNothing() {
        String upc = "0631234200036";
        BeerInventory inventory = BeerInventory.builder().upc(upc).quantityOnHand(0).build();
        given(beerInventoryRepository.findAllByUpc(upc)).willReturn(List.of(inventory));

        BeerOrderDto order = orderWith(upc, 5, 0);
        boolean result = allocationService.allocateOrder(order);

        assertFalse(result);
        assertEquals(0, order.getBeerOrderLines().getFirst().getQuantityAllocated());
        verify(beerInventoryRepository, never()).save(any(BeerInventory.class));
        verify(beerInventoryRepository, never()).delete(any(BeerInventory.class));
    }

    @Test
    void allocateOrder_multipleInventoryRecords_allocatesAcrossRecords() {
        String upc = "0631234200036";
        BeerInventory first = BeerInventory.builder().upc(upc).quantityOnHand(3).build();
        BeerInventory second = BeerInventory.builder().upc(upc).quantityOnHand(7).build();
        given(beerInventoryRepository.findAllByUpc(upc)).willReturn(List.of(first, second));

        BeerOrderDto order = orderWith(upc, 10, 0);
        boolean result = allocationService.allocateOrder(order);

        assertTrue(result);
        assertEquals(10, order.getBeerOrderLines().getFirst().getQuantityAllocated());
        verify(beerInventoryRepository).delete(first);
        verify(beerInventoryRepository).save(second);
        assertEquals(0, second.getQuantityOnHand());
    }

    @Test
    void allocateOrder_nullQuantityAllocated_isTreatedAsZero() {
        String upc = "0631234200036";
        BeerInventory inventory = BeerInventory.builder().upc(upc).quantityOnHand(5).build();
        given(beerInventoryRepository.findAllByUpc(upc)).willReturn(List.of(inventory));

        BeerOrderDto order = orderWith(upc, 5, null);
        boolean result = allocationService.allocateOrder(order);

        assertTrue(result);
        assertEquals(5, order.getBeerOrderLines().getFirst().getQuantityAllocated());
        verify(beerInventoryRepository).save(inventory);
    }

    @Test
    void allocateOrder_multipleOrderLines_returnsFalseWhenNotFullyAllocated() {
        String upc1 = "0631234200036";
        String upc2 = "0631234300019";
        BeerInventory inventory1 = BeerInventory.builder().upc(upc1).quantityOnHand(5).build();
        BeerInventory inventory2 = BeerInventory.builder().upc(upc2).quantityOnHand(2).build();
        given(beerInventoryRepository.findAllByUpc(upc1)).willReturn(List.of(inventory1));
        given(beerInventoryRepository.findAllByUpc(upc2)).willReturn(List.of(inventory2));

        BeerOrderLineDto line1 = BeerOrderLineDto.builder()
            .id(UUID.randomUUID())
            .upc(upc1)
            .orderQuantity(5)
            .quantityAllocated(0)
            .build();
        BeerOrderLineDto line2 = BeerOrderLineDto.builder()
            .id(UUID.randomUUID())
            .upc(upc2)
            .orderQuantity(5)
            .quantityAllocated(0)
            .build();
        BeerOrderDto order = BeerOrderDto.builder().id(UUID.randomUUID()).beerOrderLines(List.of(line1, line2)).build();

        boolean result = allocationService.allocateOrder(order);

        assertFalse(result);
        assertEquals(5, line1.getQuantityAllocated());
        assertEquals(2, line2.getQuantityAllocated());
        verify(beerInventoryRepository).save(inventory1);
        verify(beerInventoryRepository).delete(inventory2);
    }

}
