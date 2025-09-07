package com.pm.flightsmanagement.repos;

import com.pm.flightsmanagement.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByIdDesc(long userId);
}
