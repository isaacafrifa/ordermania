package com.iam.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
This class, as the name implies, sends the message to the RabbitMQ service using RestTemplate.
I made this class so the Inventory Controller isn't littered with all those @Value injections.
 */
@Service
public class RabbitMQSender {

    @Value("${rabbitmq.exchange}")
    private String EXCHANGE;
    @Value("${rabbitmq.routingkey}")
    private String ROUTING_KEY;
    @Value("${rabbitmq.queue}")
    private String QUEUE;
    private RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQSender.class);

    private RabbitMQSender(RabbitTemplate template){
        this.rabbitTemplate= template;
    }

    public void send(InventoryStatus inventoryStatus) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, inventoryStatus);
        LOGGER.info("MESSAGE [ DETAILS= "+inventoryStatus+" ] SENT TO RABBIT-MQ SERVICE");
    }

}
