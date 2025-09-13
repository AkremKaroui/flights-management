package com.pm.flightsmanagement.services.impl;

import com.pm.flightsmanagement.dtos.AirportDTO;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.entities.Airport;
import com.pm.flightsmanagement.enums.City;
import com.pm.flightsmanagement.enums.Country;
import com.pm.flightsmanagement.exceptions.BadRequestException;
import com.pm.flightsmanagement.exceptions.NotFoundException;
import com.pm.flightsmanagement.repos.AirportRepo;
import com.pm.flightsmanagement.services.AirportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {

    private final AirportRepo airportRepo;
    private final ModelMapper modelMapper;

    @Override
    public Response<?> createAirport(AirportDTO airportDTO) {
        log.info("Inside CreateAirport");
        Country country = airportDTO.getCountry();
        City city = airportDTO.getCity();
        if (!city.getCountry().equals(country)){
            throw new BadRequestException("City doesn't belong to country");
        }
        Airport airport = modelMapper.map(airportDTO, Airport.class);
        airportRepo.save(airport);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Ariport Created")
                .build();
    }

    @Override
    public Response<?> updateAirport(AirportDTO airportDTO) {
        Long id = airportDTO.getId();
        Airport existingAirport = airportRepo.findById(id)
                .orElseThrow(()-> new NotFoundException("Airport Not Found"));

        if(airportDTO.getName() != null){
            existingAirport.setName(airportDTO.getName());
        }
        if(airportDTO.getIataCode() != null){
            existingAirport.setIataCode(airportDTO.getIataCode());
        }
        airportRepo.save(existingAirport);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Ariport Updated")
                .build();
    }

    @Override
    public Response<List<AirportDTO>> getAllAirport() {
        List<AirportDTO> airports = airportRepo.findAll().stream()
                .map(airport -> modelMapper.map(airport, AirportDTO.class))
                .toList();

        return Response.<List<AirportDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Ariports existing")
                .data(airports)
                .build();
    }

    @Override
    public Response<AirportDTO> getAirportById(Long id) {
        Airport airport = airportRepo.findById(id).orElseThrow(()-> new NotFoundException(
                "Airport doesn't exists"));
        AirportDTO airportDTO = modelMapper.map(airport, AirportDTO.class);
        return Response.<AirportDTO> builder()
                .statusCode(HttpStatus.OK.value())
                .message("Ariports existing")
                .data(airportDTO)
                .build();
    }
}
