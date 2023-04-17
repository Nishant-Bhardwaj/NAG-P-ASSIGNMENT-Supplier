package com.nishant.flight_supplier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nishant.flight_supplier.entity.FlightBookings;
import org.springframework.stereotype.Service;

@Service
public interface RMQProducerService {

    void sendRMQConfirmationNotification(FlightBookings flightBookingResponse) throws JsonProcessingException;

    void sendRMQCancellationNotification(FlightBookings flightBookingResponse) throws JsonProcessingException;
}
