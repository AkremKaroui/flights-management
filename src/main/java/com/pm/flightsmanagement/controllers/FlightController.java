package com.pm.flightsmanagement.controllers;

import com.pm.flightsmanagement.dtos.CreateFlightRequest;
import com.pm.flightsmanagement.dtos.FlightDTO;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.enums.City;
import com.pm.flightsmanagement.enums.Country;
import com.pm.flightsmanagement.enums.FlightStatus;
import com.pm.flightsmanagement.services.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
    public ResponseEntity<Response<?>> createFlight(@Valid @RequestBody CreateFlightRequest createFlightRequest) {
        return ResponseEntity.ok(flightService.createFlight(createFlightRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<FlightDTO>> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @GetMapping
    public ResponseEntity<Response<List<FlightDTO>>> getAllFlight() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
    public ResponseEntity<Response<?>> updateFlight(@RequestBody CreateFlightRequest createFlightRequest) {
        return ResponseEntity.ok(flightService.updateFlight(createFlightRequest));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<List<FlightDTO>>> searchFlights(
            @RequestParam(required = true) String departureAirportIataCode,
            @RequestParam(required = true) String arrivalAirportIataCode,
            @RequestParam(required = false, defaultValue = "SCHEDULED") FlightStatus flightStatus,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate
    ) {
        return ResponseEntity.ok(flightService.searchFlight(
                departureAirportIataCode, arrivalAirportIataCode, flightStatus, departureDate
        ));
    }

    @GetMapping("/cities")
    public ResponseEntity<Response<List<City>>> getAllCities() {
        return ResponseEntity.ok(flightService.getAllCities());
    }

    @GetMapping("/countries")
    public ResponseEntity<Response<List<Country>>> getAllCountries() {
        return ResponseEntity.ok(flightService.getAllCountries());
    }
}
