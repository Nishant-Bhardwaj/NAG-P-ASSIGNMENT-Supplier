package com.nishant.flight_supplier.repository;

import com.nishant.flight_supplier.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query(
            value = "select * from tbl_flight tt where tt.name=:flightName AND tt.departure_location=:depLocation AND tt.arrival_location=:arrLocation AND tt.flight_class=:flightClass AND tt.flight_date=:flightDate",
            nativeQuery = true
    )
    List<Flight> findByFilter(@Param("flightName") String flightName,
                              @Param("depLocation") String depLocation,
                              @Param("arrLocation") String arrLocation,
                              @Param("flightClass") String flightClass,
                              @Param("flightDate") LocalDate flightDate);


    @Query(
            value = "select * from tbl_flight tt where tt.name=:flightName",
            nativeQuery = true
    )
    List<Flight> findByFilterName(@Param("flightName") String flightName);
}
