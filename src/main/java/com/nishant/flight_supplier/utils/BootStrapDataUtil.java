package com.nishant.flight_supplier.utils;

import com.nishant.flight_supplier.entity.Flight;
import com.nishant.flight_supplier.repository.FlightBookingsRepo;
import com.nishant.flight_supplier.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class BootStrapDataUtil {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    FlightBookingsRepo flightBookingsRepo;

    @PostConstruct
    private void createFlightBootstrapData(){

        flightBookingsRepo.deleteAll();
        flightRepository.deleteAll();

        ArrayList<Flight> flightArrayList = new ArrayList<>();

        flightArrayList.add(Flight.builder()
                        .name("Air India").arrivalLocation("Delhi")
                        .departureLocation("Pune").flightClass("Economy").price(1000L)
                        .flightDate(LocalDate.of(2023,03,25)).seatsAvailable(20)
                        .build()
        );

        flightArrayList.add(Flight.builder()
                .name("Air India").arrivalLocation("Pune")
                .departureLocation("Jaipur").flightClass("Economy").price(2000L)
                .flightDate(LocalDate.of(2023,03,25)).seatsAvailable(40)
                .build()
        );

        flightArrayList.add(Flight.builder()
                .name("Indigo").arrivalLocation("Delhi")
                .departureLocation("Mumbai").flightClass("Business").price(3000L)
                .flightDate(LocalDate.of(2023,03,25)).seatsAvailable(55)
                .build()
        );

        flightArrayList.add(Flight.builder()
                .name("Jet Airlines").arrivalLocation("Delhi")
                .departureLocation("Pune").flightClass("Business").price(4000L)
                .flightDate(LocalDate.of(2023,03,25)).seatsAvailable(40)
                .build()
        );


        flightArrayList.forEach(x-> flightRepository.save(x));
    }
}
