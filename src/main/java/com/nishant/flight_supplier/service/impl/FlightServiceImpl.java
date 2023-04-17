package com.nishant.flight_supplier.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nishant.flight_supplier.dto.FlightBookingRequest;
import com.nishant.flight_supplier.entity.Flight;
import com.nishant.flight_supplier.entity.FlightBookings;
import com.nishant.flight_supplier.repository.FlightBookingsRepo;
import com.nishant.flight_supplier.repository.FlightRepository;
import com.nishant.flight_supplier.service.FlightService;
import com.nishant.flight_supplier.service.RMQProducerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class FlightServiceImpl implements FlightService {

    Logger logger = LogManager.getLogger(FlightServiceImpl.class);

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    RMQProducerService rmqProducerService;

    @Autowired
    FlightBookingsRepo flightBookingsRepo;

    @Override
    public Mono<String> searchFlight(Flight flight) throws JsonProcessingException {

        logger.info("SearchFlight Impl: "+ flight.getName());

        String flightName = flight.getName();
        String depLocation = flight.getDepartureLocation();
        String arrLocation = flight.getArrivalLocation();
        String flightClass = flight.getFlightClass();
        LocalDate flightDate = flight.getFlightDate();

        List<Flight> flightList =
                flightRepository.findByFilter(flightName , depLocation, arrLocation, flightClass, flightDate);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String responseJson = mapper.writeValueAsString(flightList);

        return Mono.just(responseJson);
    }

    @Override
    public List<Flight> findAllFlights() {
        logger.info("Find all flights: START ");

        List<Flight> flightList = flightRepository.findAll();
        flightList.forEach(logger::info);

        return flightList;
    }

    @Override
    public FlightBookings bookFlight(FlightBookingRequest flightBookingRequest) throws Exception {
        logger.info("Book Flight: " + flightBookingRequest);

        // Logic to book flight:

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 1. Find and validate Flight:
        Optional<Flight> flight = flightRepository.findById(flightBookingRequest.getFlightId());

        if(!flight.isPresent()){
            throw new Exception("Flight Not Found!");
        }

        // 2. verify seats are pending:
        if(flight.get().getSeatsAvailable()<1){
            throw new Exception("All Seats Booked!");
        }
        else {

            FlightBookings flightBookings = FlightBookings.builder()
                    .flight(flight.get())
                    .seatNumber("After Payment")
                    .status("Payment Pending")
                    .userId(flightBookingRequest.getUserId())
                    .build();

            // 3. persist flight booking:
            flightBookings = flightBookingsRepo.save(flightBookings);

            return flightBookings;
        }
    }

    @Override
    public void bookingPaymentAndConfirm(FlightBookings flightBookings) throws Exception {

        logger.info("Payment Request Received!");

        // 1. Find and validate Flight:
        Optional<Flight> flight = flightRepository.findById(flightBookings.getFlight().getFlightId());

        if(!flight.isPresent()){
            sendBookingFailedNotification(flightBookings, "Invalid Request!");
            throw new Exception("Invalid Request!");
        }

        else if(flight.get().getSeatsAvailable()<1){
            sendBookingFailedNotification(flightBookings, "Sorry, All seats booked");
            throw new Exception("Sorry, All seats booked");
        }
        else{
            logger.info("Payment is Successful!");

            // reduce seat count:
            flight.get().setSeatsAvailable(flight.get().getSeatsAvailable()-1);

            // save/update flight:
            flightRepository.save(flight.get());

            // send notification:
            sendBookingSuccessNotification(flightBookings, "");
        }
    }

    @Override
    public void sendBookingFailedNotification(FlightBookings flightBookings, String message) throws Exception {

        logger.info("Send Booking Failed Notification");

        // 1. Find and validate Flight:
        Optional<Flight> flight = flightRepository.findById(flightBookings.getFlight().getFlightId());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Update status:
        flightBookings.setSeatNumber("Not Allocated");
        flightBookings.setStatus("Failed, REFUND Initiated "+ message);

        // Notification for Booking Cancellation:
        rmqProducerService.sendRMQCancellationNotification(flightBookings);
    }


    @Override
    public void sendBookingSuccessNotification(FlightBookings flightBookings, String message) throws Exception {

        logger.info("Send Booking Success Notification");

        // 1. Find and validate Flight:
        Optional<Flight> flight = flightRepository.findById(flightBookings.getFlight().getFlightId());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Fetch flight Booking:
        Optional<FlightBookings> flightBookingsFinal = flightBookingsRepo.findById(flightBookings.getId());

        if(!flightBookingsFinal.isPresent()){
            throw new Exception("Invalid Request!");
        }
        FlightBookings flightBookingsResponse = flightBookingsFinal.get();

        // Update status:
        flightBookingsResponse.setSeatNumber(String.valueOf(flight.get().getSeatsAvailable()));
        flightBookingsResponse.setStatus("Confirmed/Booked");

        // 2. persist flight booking:
        flightBookingsResponse = flightBookingsRepo.save(flightBookingsResponse);

        // Notification for Booking Confirmation:
        rmqProducerService.sendRMQConfirmationNotification(flightBookingsResponse);
    }
}
