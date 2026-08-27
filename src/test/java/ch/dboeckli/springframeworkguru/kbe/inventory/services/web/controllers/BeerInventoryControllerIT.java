package ch.dboeckli.springframeworkguru.kbe.inventory.services.web.controllers;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.domain.BeerInventory;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.repositories.BeerInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.docker.compose.skip.in-tests=false" })
@AutoConfigureMockMvc
@Slf4j
class BeerInventoryControllerIT {

    private static final String USER = "good";

    private static final String PASSWORD = "beer";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BeerInventoryRepository beerInventoryRepository;

    UUID beerId;

    @BeforeEach
    void setUp() {
        beerInventoryRepository.deleteAll();
        beerId = UUID.randomUUID();
        beerInventoryRepository
            .save(BeerInventory.builder().beerId(beerId.toString()).upc("0631234200036").quantityOnHand(10).build());
    }

    @AfterEach
    void tearDown() {
        beerInventoryRepository.deleteAll();
    }

    @Test
    void listBeersById_authenticated_returnsInventory() throws Exception {
        mockMvc
            .perform(get("/api/v1/beer/{beerId}/inventory", beerId).header(HttpHeaders.AUTHORIZATION,
                    basicAuth(USER, PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].beerId").value(beerId.toString()))
            .andExpect(jsonPath("$[0].quantityOnHand").value(10));
    }

    @Test
    void listAllBeers_authenticated_returnsInventory() throws Exception {
        mockMvc.perform(get("/api/v1/beer/inventory").header(HttpHeaders.AUTHORIZATION, basicAuth(USER, PASSWORD)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].beerId").value(beerId.toString()));
    }

    @Test
    void listBeersById_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/beer/{beerId}/inventory", beerId)).andExpect(status().isUnauthorized());
    }

    @Test
    void listBeersById_withInvalidCredentials_returnsUnauthorized() throws Exception {
        mockMvc
            .perform(get("/api/v1/beer/{beerId}/inventory", beerId).header(HttpHeaders.AUTHORIZATION,
                    basicAuth(USER, "wrong")))
            .andExpect(status().isUnauthorized());
    }

    private String basicAuth(String user, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

}
