package ch.dboeckli.springframeworkguru.kbe.inventory.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@ActiveProfiles("it-test")
@Slf4j
class InventoryServiceApplicationTest {

    @Test
    void contextLoads() {
        log.info("Testing Spring 6 Application...");
    }

}
