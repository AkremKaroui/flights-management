package com.pm.flightsmanagement.controllers;

import com.pm.flightsmanagement.dtos.AirportDTO;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.dtos.UserDTO;
import com.pm.flightsmanagement.services.AirportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {
    private final AirportService airportService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> createAirport(@Valid @RequestBody AirportDTO airportDTO) {
        return ResponseEntity.ok(airportService.createAirport(airportDTO));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> updateAirport(@RequestBody AirportDTO airportDTO) {
        return ResponseEntity.ok(airportService.updateAirport(airportDTO));
    }

    @GetMapping
    public ResponseEntity<Response<List<AirportDTO>>> getAllAirport() {
        return ResponseEntity.ok(airportService.getAllAirport());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<AirportDTO>> getAllAirport(@PathVariable Long id) {
        return ResponseEntity.ok(airportService.getAirportById(id));
    }


}
