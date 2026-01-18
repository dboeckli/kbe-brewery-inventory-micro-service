package ch.dboeckli.springframeworkguru.kbe.inventory.services.listeners;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.services.AllocationService;
import ch.guru.springframework.kbe.lib.events.AllocateBeerOrderRequest;
import ch.guru.springframework.kbe.lib.events.AllocateBeerOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @Value("${sfg.brewery.queues.allocate-order-result}")
    String allocatOrderResultQueue;

    @JmsListener(destination = "${sfg.brewery.queues.allocate-order}")
    public void listen(AllocateBeerOrderRequest request) {
        log.info("Allocating Order: " + request.getBeerOrder().getId());

        AllocateBeerOrderResult.AllocateBeerOrderResultBuilder builder = AllocateBeerOrderResult.builder();
        builder.beerOrderDto(request.getBeerOrder());

        try {
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrder());
            builder.pendingInventory(!allocationResult);
            builder.allocationError(false);
            jmsTemplate.convertAndSend(allocatOrderResultQueue, builder.build());
            log.info("Allocated Order {} placed on queue: {}", request.getBeerOrder().getId(), allocatOrderResultQueue);
        } catch (Exception e) {
            //some error occured
            builder.allocationError(true).pendingInventory(false);
            log.error("Allocation attempt failed for order id " + request.getBeerOrder().getId(), e);
        }

    }
}
