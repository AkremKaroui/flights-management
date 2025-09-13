package com.pm.flightsmanagement.services;

import com.pm.flightsmanagement.dtos.CreateFlightRequest;
import com.pm.flightsmanagement.dtos.FlightDTO;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.enums.City;
import com.pm.flightsmanagement.enums.Country;
import com.pm.flightsmanagement.enums.FlightStatus;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    Response<?> createFlight(CreateFlightRequest createFlightRequest);
    Response<FlightDTO> getFlightById(Long id);
    Response<List<FlightDTO>> getAllFlights();
    Response<?> updateFlight(CreateFlightRequest createFlightRequest);
    Response<List<FlightDTO>> searchFlight(String departureAirportIata, String arrivalAiportIata,
                             FlightStatus flightStatus, LocalDate departureDate);
    Response<List<City>> getAllCities();
    Response<List<Country>> getAllCountries();
}
