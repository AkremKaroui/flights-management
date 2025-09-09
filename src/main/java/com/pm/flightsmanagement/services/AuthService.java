package com.pm.flightsmanagement.services;

import com.pm.flightsmanagement.dtos.LoginRequest;
import com.pm.flightsmanagement.dtos.LoginResponse;
import com.pm.flightsmanagement.dtos.RegistrationRequest;
import com.pm.flightsmanagement.dtos.Response;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    Response<?> register(RegistrationRequest registrationRequest);
    Response<LoginResponse> login(LoginRequest loginRequest);
}
