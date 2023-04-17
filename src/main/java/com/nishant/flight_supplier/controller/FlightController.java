package com.nishant.flight_supplier.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nishant.flight_supplier.dto.FlightBookingRequest;
import com.nishant.flight_supplier.entity.Flight;
import com.nishant.flight_supplier.entity.FlightBookings;
import com.nishant.flight_supplier.service.FlightService;
import io.jaegertracing.internal.utils.Http;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/supplier/flight")
public class FlightController {

    Logger logger = LogManager.getLogger(FlightController.class);

    @Autowired
    FlightService flightService;

    // Search All Flights
    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllFlights() {
        logger.info("Request Received, Search All Flights");
        return new ResponseEntity<>(flightService.findAllFlights(), HttpStatus.OK);
    }

    // Search with parameters
    @PostMapping(path = "/search")
    public Mono<String> getSearchedFlights(@RequestBody Flight flight) throws JsonProcessingException {
        logger.info("Request Received, Search Flight : " + flight);
        return flightService.searchFlight(flight);
    }

    // Flight booking request
    @PostMapping(path = "/book")
    public ResponseEntity<FlightBookings> flightBooking(@RequestBody FlightBookingRequest flightBookingRequest) throws Exception {
        logger.info("Request for Flight Booking : " + flightBookingRequest);
        return new ResponseEntity<>(flightService.bookFlight(flightBookingRequest), HttpStatus.OK);
    }

    // Flight booking payment
    @PostMapping(path = "/payment")
    public ResponseEntity<String> bookingPayment(@RequestBody FlightBookings flightBookings) throws Exception {
        logger.info("Request for Flight Payment : " + flightBookings);

        CompletableFuture.runAsync(() -> {
            try {
                flightService.bookingPaymentAndConfirm(flightBookings);
            } catch (Exception e) {
                logger.error("Sent failure Notification for: " + e.getMessage());
            }
        });

        return new ResponseEntity<>(
                "Notification is sent to Consumer App via RMQ.",
                HttpStatus.OK
        );
    }

}
