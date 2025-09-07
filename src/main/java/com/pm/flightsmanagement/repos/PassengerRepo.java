package com.pm.flightsmanagement.repos;

import com.pm.flightsmanagement.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepo extends JpaRepository<Passenger,Long> {

}
