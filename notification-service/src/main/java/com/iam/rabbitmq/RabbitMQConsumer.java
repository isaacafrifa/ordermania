package com.iam.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
/*
This class, as the name implies, consumes the messages from the RabbitMQ service.
It listens to the specified queue(s)
 */
@Component
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    /* listen to the specified queue */
    @RabbitListener(queues = "inventory.queue")
    public void consumeMessageFromQueue(InventoryStatus inventoryStatus) {
        LOGGER.info("MESSAGE RECEIVED FROM RABBIT-MQ SERVICE [DETAILS = : "+inventoryStatus+" ]");
    }

//    private void processMessage(){}
}
