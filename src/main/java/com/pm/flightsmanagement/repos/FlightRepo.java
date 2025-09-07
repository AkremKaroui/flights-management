package com.pm.flightsmanagement.repos;

import com.pm.flightsmanagement.entities.Flight;
import com.pm.flightsmanagement.enums.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepo extends JpaRepository<Flight, Long> {

    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findByDepartureAirportIataCodeAndArrivalAirportIataCodeAndStatusAndDepartureTimeBetween
            (String departureAirportIataCode, String arrivalAirportIataCode,
             FlightStatus status, LocalDateTime startOfDay, LocalDateTime endOfDay
            );
}
