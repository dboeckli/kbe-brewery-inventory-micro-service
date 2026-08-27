package ch.dboeckli.springframeworkguru.kbe.inventory.services.web.mappers;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.guru.springframework.kbe.lib.dto.BeerInventoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BeerInventoryMapperTest {

    private BeerInventoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BeerInventoryMapperImpl();
        ReflectionTestUtils.setField(mapper, "dateMapper", new DateMapper());
    }

    @Test
    void beerInventoryToBeerInventoryDto_mapsAllFields() {
        UUID id = UUID.randomUUID();
        UUID beerId = UUID.randomUUID();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(2024, 5, 10, 12, 34, 56));
        BeerInventory inventory = BeerInventory.builder()
            .id(id)
            .beerId(beerId.toString())
            .quantityOnHand(42)
            .createdDate(timestamp)
            .lastModifiedDate(timestamp)
            .build();

        BeerInventoryDto dto = mapper.beerInventoryToBeerInventoryDto(inventory);

        assertEquals(id, dto.getId());
        assertEquals(beerId, dto.getBeerId());
        assertEquals(42, dto.getQuantityOnHand());
        assertEquals(OffsetDateTime.of(2024, 5, 10, 12, 34, 56, 0, ZoneOffset.UTC), dto.getCreatedDate());
        assertEquals(OffsetDateTime.of(2024, 5, 10, 12, 34, 56, 0, ZoneOffset.UTC), dto.getLastModifiedDate());
    }

    @Test
    void beerInventoryToBeerInventoryDto_returnsNullForNull() {
        assertNull(mapper.beerInventoryToBeerInventoryDto(null));
    }

    @Test
    void beerInventoryDtoToBeerInventory_mapsAllFields() {
        UUID id = UUID.randomUUID();
        UUID beerId = UUID.randomUUID();
        OffsetDateTime createdDate = OffsetDateTime.of(2024, 5, 10, 12, 34, 56, 0, ZoneOffset.UTC);
        BeerInventoryDto dto = BeerInventoryDto.builder()
            .id(id)
            .beerId(beerId)
            .quantityOnHand(7)
            .createdDate(createdDate)
            .lastModifiedDate(createdDate)
            .build();

        BeerInventory inventory = mapper.beerInventoryDtoToBeerInventory(dto);

        assertEquals(id, inventory.getId());
        assertEquals(beerId.toString(), inventory.getBeerId());
        assertEquals(7, inventory.getQuantityOnHand());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2024, 5, 10, 12, 34, 56)), inventory.getCreatedDate());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2024, 5, 10, 12, 34, 56)), inventory.getLastModifiedDate());
    }

    @Test
    void beerInventoryDtoToBeerInventory_returnsNullForNull() {
        assertNull(mapper.beerInventoryDtoToBeerInventory(null));
    }

}
