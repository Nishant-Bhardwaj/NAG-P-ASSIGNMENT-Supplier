package com.nishant.flight_supplier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nishant.flight_supplier.dto.FlightBookingRequest;
import com.nishant.flight_supplier.entity.Flight;
import com.nishant.flight_supplier.entity.FlightBookings;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface FlightService {

    Mono<String> searchFlight(Flight flight) throws JsonProcessingException;

    List<Flight> findAllFlights();

    FlightBookings bookFlight(FlightBookingRequest flightBookingRequest) throws Exception;

    void sendBookingFailedNotification(FlightBookings flightBookings, String message) throws Exception;

    void sendBookingSuccessNotification(FlightBookings flightBookings, String message) throws JsonProcessingException, Exception;

    void bookingPaymentAndConfirm(FlightBookings flightBookings) throws Exception;
}
