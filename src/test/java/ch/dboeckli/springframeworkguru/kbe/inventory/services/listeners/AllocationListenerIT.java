package ch.dboeckli.springframeworkguru.kbe.inventory.services.listeners;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.services.AllocationService;
import ch.guru.springframework.kbe.lib.dto.BeerOrderDto;
import ch.guru.springframework.kbe.lib.events.AllocateBeerOrderRequest;
import ch.guru.springframework.kbe.lib.events.AllocateBeerOrderResult;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest(properties = {
    "sfg.brewery.queues.allocate-order-result=allocate-order-result-test",
    "spring.docker.compose.skip.in-tests=false"
})
@Slf4j
class AllocationListenerIT {

    @Value("${sfg.brewery.queues.allocate-order-result}")
    String allocateOrderResultQueue;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AllocationService allocationServiceMock;

    @Autowired
    AllocationListener allocationListener;

    @Test
    void testAllocationListener() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        AllocateBeerOrderRequest request = AllocateBeerOrderRequest.builder()
            .beerOrder(BeerOrderDto.builder()
                .id(orderId)
                .build())
            .build();

        given(allocationServiceMock.allocateOrder(request.getBeerOrder())).willReturn(true);

        // Act
        allocationListener.listen(request);

        // Assert
        AllocateBeerOrderResult result = awaitEventInQueue(allocateOrderResultQueue, orderId);
        assertNotNull(result, "Result should be received in queue");
        assertNotNull(result.getBeerOrderDto());
        assertEquals(orderId, result.getBeerOrderDto().getId());
    }

    @Test
    void testAllocationListenerWithFailure() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        AllocateBeerOrderRequest request = AllocateBeerOrderRequest.builder()
            .beerOrder(BeerOrderDto.builder()
                .id(orderId)
                .build())
            .build();

        given(allocationServiceMock.allocateOrder(request.getBeerOrder())).willThrow(new RuntimeException());

        // Act
        allocationListener.listen(request);

        // Assert
        AllocateBeerOrderResult result = awaitEventInQueue(allocateOrderResultQueue, orderId);
        assertNull(result);
    }


    private AllocateBeerOrderResult awaitEventInQueue(String queueName, UUID expectedOrderId) {
        AtomicReference<AllocateBeerOrderResult> foundEventRef = new AtomicReference<>();

        // Wir setzen ein kurzes Timeout für den JMS-Receive, damit Awaitility die Schleife steuern kann
        jmsTemplate.setReceiveTimeout(100);

        try {
            Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> {
                    Message message = jmsTemplate.receive(queueName);

                    if (message instanceof TextMessage textMessage) {
                        try {
                            String payload = textMessage.getText();
                            AllocateBeerOrderResult event = objectMapper.readValue(payload, AllocateBeerOrderResult.class);
                            log.info("Got event: {}", event);

                            if (event.getBeerOrderDto() != null && expectedOrderId.equals(event.getBeerOrderDto().getId())) {
                                foundEventRef.set(event);
                                return true; // Gefunden!
                            } else {
                                log.debug("Ignoriere Nachricht für andere ID: {}", event.getBeerOrderDto().getId());
                            }
                        } catch (Exception e) {
                            log.warn("Konnte Nachricht nicht deserialisieren: {}", e.getMessage());
                        }
                    }
                    return false; // Weiter suchen
                });
        } catch (Exception e) {
            // Awaitility wirft eine Exception bei Timeout -> wir geben null zurück oder lassen den Test hier failen
            return null;
        }

        return foundEventRef.get();
    }

}