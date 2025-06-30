package ch.dboeckli.springframeworkguru.kbe.inventory.services.listeners;

import ch.dboeckli.springframeworkguru.kbe.inventory.services.config.JmsConfig;
import ch.dboeckli.springframeworkguru.kbe.inventory.services.services.AllocationService;
import ch.guru.springframework.kbe.lib.events.AllocateBeerOrderRequest;
import ch.guru.springframework.kbe.lib.events.AllocateBeerOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateBeerOrderRequest request){
        log.info("Allocating Order: " + request.getBeerOrder().getId());

        AllocateBeerOrderResult.AllocateBeerOrderResultBuilder builder = AllocateBeerOrderResult.builder();
        builder.beerOrderDto(request.getBeerOrder());

        try {
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrder());
            builder.pendingInventory(!allocationResult);
            builder.allocationError(false);
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE, builder.build());
            log.info("Allocated Order {} placed on queue: {}", request.getBeerOrder().getId(), JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE);
        } catch (Exception e) {
            //some error occured
            builder.allocationError(true).pendingInventory(false);
            log.error("Allocation attempt failed for order id " + request.getBeerOrder().getId(), e);
        }

    }
}
