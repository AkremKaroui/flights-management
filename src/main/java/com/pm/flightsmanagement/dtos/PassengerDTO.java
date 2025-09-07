package com.pm.flightsmanagement.dtos;

import com.pm.flightsmanagement.enums.PassengerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PassengerDTO {
    private Long id;

    @NotBlank(message = "Firstname cannot be blank")
    private String firstName;

    @NotBlank(message = "Firstname cannot be blank")
    private String lastName;

    @NotBlank(message = "Firstname cannot be blank")
    private String passportNumber;

    @NotNull(message = "Passenger type cannot be null")

    private PassengerType type;
    private String seatNumber;
    private String specialRequest;
}
