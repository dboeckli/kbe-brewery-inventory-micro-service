package ch.dboeckli.springframeworkguru.kbe.inventory.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import tools.jackson.databind.json.JsonMapper;

//@EnableJms
@Configuration
public class JmsConfig {
    public static final String ALLOCATE_ORDER_QUEUE = "allocate-order";
    public static final String ALLOCATE_ORDER_RESULT_QUEUE = "allocate-order-result";
    public static final String NEW_INVENTORY_QUEUE = "new-inventory";

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter(JsonMapper objectMapper) {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(objectMapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
