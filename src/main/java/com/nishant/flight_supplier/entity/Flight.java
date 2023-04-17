package com.nishant.flight_supplier.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "tbl_flight")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    private String name;

    private String departureLocation;

    private String arrivalLocation;

    private String flightClass;

    private LocalDate flightDate;

    private int seatsAvailable;

    private Long price;

    @Transient
    @JsonIgnore
    @OneToMany(
            mappedBy = "flight",
            cascade = CascadeType.REMOVE
            // Cascade the remove operation of flight entity to booking
            // i.e., if flight is deleted from flight entity,
            // bookings mapped to that flight will be deleted as well
    )
    private List<FlightBookings> flightBookingsList = new ArrayList<>();
}
