package com.pm.flightsmanagement.dtos;


import com.pm.flightsmanagement.enums.FlightStatus;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDTO {
    private Long id;

    private String flightNumber;

    @Enumerated
    private FlightStatus status;

    private AirportDTO departureAirport;

    private AirportDTO arrivalAirport;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private BigDecimal basePrice;

    private UserDTO pilot;

    private List<BookingDTO> bookings;

    private String departureAirportIataCode;
    private String arrivalAirportIataCode;

}
