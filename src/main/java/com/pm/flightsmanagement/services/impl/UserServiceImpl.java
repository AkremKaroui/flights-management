package com.pm.flightsmanagement.services.impl;

import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.dtos.UserDTO;
import com.pm.flightsmanagement.entities.User;
import com.pm.flightsmanagement.exceptions.NotFoundException;
import com.pm.flightsmanagement.repos.RoleRepo;
import com.pm.flightsmanagement.repos.UserRepo;
import com.pm.flightsmanagement.security.JwtUtils;
import com.pm.flightsmanagement.services.EmailNotificationService;
import com.pm.flightsmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(()-> new NotFoundException("User Not " +
                "Found"));
    }

    @Override
    @Transactional
    public Response<?> updateMyAccount(UserDTO userDTO) {
        log.info("inside update user account");
        User user = currentUser();

        if (userDTO.getName() != null && !userDTO.getName().isBlank()){
            user.setName(userDTO.getName());
        }
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isBlank()){
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepo.save(user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User updated")
                .build();
    }

    @Override
    public Response<List<UserDTO>> getAllPilots() {
        log.info("inside getAllPilots");
        List<UserDTO> pilots = userRepo.findByRoleName("PILOT").stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
        return Response.<List<UserDTO>> builder()
                .statusCode(HttpStatus.OK.value())
                .message(pilots.isEmpty() ? "No Pilots Found" : "Pilots existing")
                .data(pilots)
                .build();
    }

    @Override
    public Response<UserDTO> getAccountDetails() {
        log.info("inside getAccountDetails");
        User user = currentUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return Response.<UserDTO> builder()
                .statusCode(HttpStatus.OK.value())
                .message("User details")
                .data(userDTO)
                .build();
    }
}
