package com.pm.flightsmanagement.controllers;

import com.pm.flightsmanagement.dtos.*;
import com.pm.flightsmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping
    public ResponseEntity<Response<?>> updateMyAccount(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateMyAccount(userDTO));
    }

    @GetMapping("/pilots")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
    public ResponseEntity<Response<List<UserDTO>>> getAllPilots() {
        return ResponseEntity.ok(userService.getAllPilots());
    }

    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getAccountDetails() {
        return ResponseEntity.ok(userService.getAccountDetails());
    }
}
