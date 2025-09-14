package com.pm.flightsmanagement.services.impl;

import com.pm.flightsmanagement.dtos.CreateFlightRequest;
import com.pm.flightsmanagement.dtos.FlightDTO;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.entities.Airport;
import com.pm.flightsmanagement.entities.Flight;
import com.pm.flightsmanagement.entities.User;
import com.pm.flightsmanagement.enums.City;
import com.pm.flightsmanagement.enums.Country;
import com.pm.flightsmanagement.enums.FlightStatus;
import com.pm.flightsmanagement.exceptions.BadRequestException;
import com.pm.flightsmanagement.exceptions.NotFoundException;
import com.pm.flightsmanagement.repos.AirportRepo;
import com.pm.flightsmanagement.repos.FlightRepo;
import com.pm.flightsmanagement.repos.UserRepo;
import com.pm.flightsmanagement.services.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepo flightRepo;
    private final AirportRepo airportRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public Response<?> createFlight(CreateFlightRequest createFlightRequest) {
        if (createFlightRequest.getDepartureTime().isAfter(createFlightRequest.getArrivalTime())) {
            throw new BadRequestException("Arrival time cannot be before departure time");
        }
        if (flightRepo.existsByFlightNumber(createFlightRequest.getFlightNumber())) {
            throw new BadRequestException("Flight number already exists");
        }

        Airport departureAirport =
                airportRepo.findByIataCode(createFlightRequest.getDepartureAirportIataCode())
                        .orElseThrow(() -> new NotFoundException("Departure airport doesn't " +
                                "exists"));

        Airport arrivalAirport =
                airportRepo.findByIataCode(createFlightRequest.getArrivalAirportIataCode())
                        .orElseThrow(() -> new NotFoundException("Arrival airport doesn't " +
                                "exists"));

        User pilot = getPilotByUserId(createFlightRequest);

        if (arrivalAirport == departureAirport) throw new BadRequestException("Departure and " +
                "arrival airports can't be the same");


        Flight flightToSave = Flight.builder()
                .flightNumber(createFlightRequest.getFlightNumber())
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .departureTime(createFlightRequest.getDepartureTime())
                .arrivalTime(createFlightRequest.getArrivalTime())
                .basePrice(createFlightRequest.getBasePrice())
                .status(FlightStatus.SCHEDULED)
                .build();

        if (pilot != null) flightToSave.setAssignedPilot(pilot);
        flightRepo.save(flightToSave);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Flight saved")
                .build();

    }

    @Override
    public Response<FlightDTO> getFlightById(Long id) {
        Flight flight = flightRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight doesn't exists"));
        FlightDTO flightDTO = modelMapper.map(flight, FlightDTO.class);
        if (flightDTO.getBookings() != null) {
            flightDTO.getBookings().forEach(bookingDTO -> bookingDTO.setFlight(null));
        }
        return Response.<FlightDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Flight Info")
                .data(flightDTO)
                .build();
    }

    @Override
    public Response<List<FlightDTO>> getAllFlights() {
        Sort sortIdAsc = Sort.by(Sort.Direction.ASC, "id");
        List<FlightDTO> flights = flightRepo.findAll(sortIdAsc).stream()
                .map(flight -> {
                            FlightDTO flightDTO = modelMapper.map(flight, FlightDTO.class);
                            if (flightDTO.getBookings() != null) {
                                flightDTO.getBookings().forEach(bookingDTO -> bookingDTO.setFlight(null));
                            }
                            return flightDTO;
                        }
                )
                .toList();

        return Response.<List<FlightDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("List of flights registered")
                .data(flights)
                .build();
    }

    @Override
    @Transactional
    public Response<?> updateFlight(CreateFlightRequest createFlightRequest) {
        Long id = createFlightRequest.getId();
        Flight flight = flightRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight doesn't exists"));

        LocalDateTime updatedDepartureTime = createFlightRequest.getDepartureTime();
        LocalDateTime updateArrivalTime = createFlightRequest.getArrivalTime();

        //Check dates and validate order and assign them to flight
        if (updatedDepartureTime != null || updateArrivalTime != null) {
            if (updatedDepartureTime != null && updateArrivalTime != null) {
                if (updatedDepartureTime.isAfter(updateArrivalTime)) {
                    throw new BadRequestException("Arrival time cannot be before departure time");
                } else {
                    flight.setDepartureTime(updatedDepartureTime);
                    flight.setArrivalTime(updateArrivalTime);
                }
            } else {
                if (updatedDepartureTime != null) {
                    if (updatedDepartureTime.isAfter(flight.getArrivalTime())) {
                        throw new BadRequestException("Arrival time cannot be before departure " +
                                "time");
                    } else {
                        flight.setDepartureTime(updatedDepartureTime);
                    }
                } else {
                    if (flight.getDepartureTime().isAfter(updateArrivalTime)) {
                        throw new BadRequestException("Arrival time cannot be before departure " +
                                "time");
                    } else {
                        flight.setArrivalTime(updateArrivalTime);
                    }
                }
            }
        }
        // END

        if (createFlightRequest.getBasePrice() != null) {
            flight.setBasePrice(createFlightRequest.getBasePrice());
        }
        if (createFlightRequest.getFlightStatus() != null) {
            flight.setStatus(createFlightRequest.getFlightStatus());
        }

        // Validate and assign pilot if changed
        User pilot = getPilotByUserId(createFlightRequest);
        if (pilot != null) {
            flight.setAssignedPilot(pilot);
        }

        flightRepo.save(flight);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Flight updated")
                .build();
    }

    @Override
    public Response<List<FlightDTO>> searchFlight(String departureAirportIata,
                                                  String arrivalAiportIata,
                                                  FlightStatus flightStatus,
                                                  LocalDate departureDate) {
        LocalDateTime startOfDay = departureDate.atStartOfDay();
        LocalDateTime endOfDay = departureDate.plusDays(1).atStartOfDay().minusNanos(1);

        List<Flight> flights =
                flightRepo.findByDepartureAirportIataCodeAndArrivalAirportIataCodeAndStatusAndDepartureTimeBetween
                        (departureAirportIata, arrivalAiportIata, flightStatus, startOfDay,
                                endOfDay);

        List<FlightDTO> flightDTOS = flights.stream()
                .map(flight -> {
                    FlightDTO flightDTO = modelMapper.map(flight, FlightDTO.class);
                    flightDTO.setBookings(null);
                    flightDTO.setPilot(null);
                    return flightDTO;
                })
                .toList();

        return Response.<List<FlightDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("List of flights on " + departureDate)
                .data(flightDTOS)
                .build();
    }

    @Override
    public Response<List<City>> getAllCities() {
        return Response.<List<City>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("List of cities")
                .data(List.of(City.values()))
                .build();
    }

    @Override
    public Response<List<Country>> getAllCountries() {
        return Response.<List<Country>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("List of countries")
                .data(List.of(Country.values()))
                .build();
    }

    private User getPilotByUserId(CreateFlightRequest createFlightRequest) {
        User pilot = null;
        if (createFlightRequest.getPilotId() != null) {
            pilot =
                    userRepo.findById(createFlightRequest.getPilotId())
                            .orElseThrow(() -> new NotFoundException("Pilot doesn't exists"));
            boolean isPilot =
                    pilot.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(
                    "PILOT"));
            if (!isPilot) {
                throw new BadRequestException("Pilot is not registered");
            }
        }
        return pilot;
    }
}
