package ch.dboeckli.springframeworkguru.kbe.inventory.services.web.controllers;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.web.mappers.BeerInventoryMapper;
import ch.guru.springframework.kbe.lib.dto.BeerInventoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeerInventoryControllerTest {

    @Mock
    BeerInventoryRepository beerInventoryRepository;

    @Mock
    BeerInventoryMapper beerInventoryMapper;

    @InjectMocks
    BeerInventoryController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listBeersById() throws Exception {
        UUID beerId = UUID.randomUUID();
        BeerInventory inventory1 = BeerInventory.builder()
                .id(UUID.randomUUID())
                .beerId(beerId.toString())
                .quantityOnHand(10).build();
        BeerInventory inventory2 = BeerInventory.builder()
                .id(UUID.randomUUID())
                .beerId(beerId.toString())
                .quantityOnHand(20).build();
        List<BeerInventory> inventories = Arrays.asList(inventory1, inventory2);

        given(beerInventoryRepository.findAllByBeerId(beerId.toString())).willReturn(inventories);

        BeerInventoryDto dto1 = BeerInventoryDto.builder()
                .id(inventory1.getId())
                .beerId(beerId)
                .quantityOnHand(10).build();
        BeerInventoryDto dto2 = BeerInventoryDto.builder()
                .id(inventory2.getId())
                .beerId(beerId)
                .quantityOnHand(20).build();

        given(beerInventoryMapper.beerInventoryToBeerInventoryDto(any(BeerInventory.class)))
            .willReturn(dto1, dto2);

        mockMvc.perform(get("/api/v1/beer/" + beerId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].beerId").value(beerId.toString()))
            .andExpect(jsonPath("$[0].quantityOnHand").value(10))
            .andExpect(jsonPath("$[1].beerId").value(beerId.toString()))
            .andExpect(jsonPath("$[1].quantityOnHand").value(20));
    }

    @Test
    void listAllBeers() throws Exception {
        BeerInventory inventory1 = BeerInventory.builder()
                .id(UUID.randomUUID())
                .beerId(UUID.randomUUID().toString())
                .quantityOnHand(10).build();
        BeerInventory inventory2 = BeerInventory.builder()
                .id(UUID.randomUUID())
                .beerId(UUID.randomUUID().toString())
                .quantityOnHand(20).build();
        List<BeerInventory> inventories = Arrays.asList(inventory1, inventory2);

        given(beerInventoryRepository.findAll()).willReturn(inventories);

        BeerInventoryDto dto1 = BeerInventoryDto.builder()
                .id(inventory1.getId())
                .beerId(UUID.fromString(inventory1.getBeerId()))
                .quantityOnHand(10).build();
        BeerInventoryDto dto2 = BeerInventoryDto.builder()
                .id(inventory2.getId())
                .beerId(UUID.fromString(inventory2.getBeerId()))
                .quantityOnHand(20).build();

        given(beerInventoryMapper.beerInventoryToBeerInventoryDto(any(BeerInventory.class)))
            .willReturn(dto1, dto2);

        mockMvc.perform(get("/api/v1/beer/inventory")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].beerId").value(inventory1.getBeerId()))
            .andExpect(jsonPath("$[0].quantityOnHand").value(10))
            .andExpect(jsonPath("$[1].beerId").value(inventory2.getBeerId()))
            .andExpect(jsonPath("$[1].quantityOnHand").value(20));
    }
}
