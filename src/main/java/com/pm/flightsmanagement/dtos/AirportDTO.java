package com.pm.flightsmanagement.dtos;

import com.pm.flightsmanagement.enums.City;
import com.pm.flightsmanagement.enums.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportDTO {

    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "name is required")
    private City city;

    @NotNull(message = "name is required")
    private Country country;

    @NotNull(message = "name is required")
    private String iataCode;
}
