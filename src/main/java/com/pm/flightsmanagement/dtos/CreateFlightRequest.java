package com.pm.flightsmanagement.dtos;

import com.pm.flightsmanagement.enums.FlightStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFlightRequest {

    private Long id;

    private FlightStatus flightStatus;

    @NotBlank(message = "flight number cannot be blank")
    private String flightNumber;

    @NotBlank(message = "Departure Airport Iata Code cannot be blank")
    private String departureAirportIataCode;

    @NotBlank(message = "Arrival Airport Iata Code cannot be blank")
    private String arrivalAirportIataCode;

    @NotNull(message = "Departure Time cannot be null")
    private LocalDateTime departureTime;

    @NotNull(message = "Departure Time cannot be null")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Base price cannot be null")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    private Long pilotId;
}
