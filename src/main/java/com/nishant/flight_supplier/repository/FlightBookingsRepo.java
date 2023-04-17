package com.nishant.flight_supplier.repository;

import com.nishant.flight_supplier.entity.FlightBookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightBookingsRepo extends JpaRepository<FlightBookings, Long> {


}
