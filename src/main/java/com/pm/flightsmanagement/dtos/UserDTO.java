package com.pm.flightsmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pm.flightsmanagement.entities.Role;
import com.pm.flightsmanagement.enums.AuthMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    private String name;
    private String email;
    private String phoneNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private boolean emailVerified;
    private AuthMethod provider;
    private String providerId;
    private List<Role> roles;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
