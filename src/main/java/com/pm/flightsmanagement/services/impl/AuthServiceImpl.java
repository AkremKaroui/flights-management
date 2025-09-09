package com.pm.flightsmanagement.services.impl;

import com.pm.flightsmanagement.dtos.LoginRequest;
import com.pm.flightsmanagement.dtos.LoginResponse;
import com.pm.flightsmanagement.dtos.RegistrationRequest;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.entities.Role;
import com.pm.flightsmanagement.entities.User;
import com.pm.flightsmanagement.enums.AuthMethod;
import com.pm.flightsmanagement.exceptions.BadRequestException;
import com.pm.flightsmanagement.exceptions.NotFoundException;
import com.pm.flightsmanagement.repos.RoleRepo;
import com.pm.flightsmanagement.repos.UserRepo;
import com.pm.flightsmanagement.security.JwtUtils;
import com.pm.flightsmanagement.services.AuthService;
import com.pm.flightsmanagement.services.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepo roleRepo;
    private final EmailNotificationService emailNotificationService;

    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {
        log.info("Inside register()");
        if (userRepo.existsByEmail(registrationRequest.getEmail())){
            throw new BadRequestException("Email already exists");
        }
        List<Role> userRoles;
        if(registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()){
            userRoles = registrationRequest.getRoles().stream()
                    .map(roleName-> roleRepo.findByName(roleName.toUpperCase())
                            .orElseThrow(() -> new NotFoundException("Role " + roleName + "Not " +
                "Found")))
                    .toList();
        }else {
            Role defaultRole = roleRepo.findByName("CUSTOMER")
                    .orElseThrow(()-> new NotFoundException("Role Customer does't exists"));
            userRoles = List.of(defaultRole);
        }
        User userToSave = new User();
        userToSave.setName(registrationRequest.getName());
        userToSave.setEmail(registrationRequest.getEmail());
        userToSave.setPhoneNumber(registrationRequest.getPhoneNumber());
        userToSave.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        userToSave.setRoles(userRoles);
        userToSave.setCreatedAt(LocalDateTime.now());
        userToSave.setUpdatedAt(LocalDateTime.now());
        userToSave.setProvider(AuthMethod.LOCAL);
        userToSave.setActive(true);
        User savedUser = userRepo.save(userToSave);
        emailNotificationService.sendWelcomeEmail(savedUser);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User Created")
                .build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        log.info("Inside login()");
        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new NotFoundException("User does not exists"));

        if (!user.isActive()) throw new NotFoundException("Account Not Active");

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("User not exists (password)");
        }

        String token = jwtUtils.generateToken(user.getEmail());
        List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setRoles(roleNames);
        loginResponse.setToken(token);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User logged in")
                .data(loginResponse)
                .build();
    }
}
