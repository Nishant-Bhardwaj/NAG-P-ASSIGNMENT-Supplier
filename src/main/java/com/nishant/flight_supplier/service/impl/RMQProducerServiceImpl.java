package com.nishant.flight_supplier.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nishant.flight_supplier.entity.FlightBookings;
import com.nishant.flight_supplier.service.RMQProducerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RMQProducerServiceImpl implements RMQProducerService {

    Logger logger = LogManager.getLogger(RMQProducerServiceImpl.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routingKey_confirm}")
    private String routingKeyConfirmation;

    @Value("${spring.rabbitmq.routingKey_cancel}")
    private String routingKeyCancel;

    @Override
    public void sendRMQConfirmationNotification(FlightBookings flightBookingResponse) throws JsonProcessingException {
        logger.info("Send RMQ Confirmation Message: "+ flightBookingResponse);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        rabbitTemplate.convertAndSend(
                exchange, routingKeyConfirmation,
                objectMapper.writeValueAsString(flightBookingResponse)
        );
    }

    @Override
    public void sendRMQCancellationNotification(FlightBookings flightBookingResponse) throws JsonProcessingException {
        logger.info("Send RMQ Cancellation Message: "+ flightBookingResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        rabbitTemplate.convertAndSend(
                exchange, routingKeyCancel,
                objectMapper.writeValueAsString(flightBookingResponse)
        );
    }
}
