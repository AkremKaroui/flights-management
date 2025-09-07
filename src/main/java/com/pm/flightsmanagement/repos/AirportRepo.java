package com.pm.flightsmanagement.repos;

import com.pm.flightsmanagement.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirportRepo extends JpaRepository<Airport, Long> {
    Optional<Airport> findByIataCode(String iataCode);
}
