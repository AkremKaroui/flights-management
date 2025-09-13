package com.pm.flightsmanagement.services;

import com.pm.flightsmanagement.dtos.AirportDTO;
import com.pm.flightsmanagement.dtos.Response;

import java.util.List;

public interface AirportService {
    Response<?> createAirport(AirportDTO airportDTO);
    Response<?> updateAirport(AirportDTO airportDTO);
    Response<List<AirportDTO>> getAllAirport();
    Response<AirportDTO> getAirportById(Long id);

}
