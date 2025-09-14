package com.pm.flightsmanagement.controllers;

import com.pm.flightsmanagement.dtos.BookingDTO;
import com.pm.flightsmanagement.dtos.CreateBookingRequest;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.enums.BookingStatus;
import com.pm.flightsmanagement.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Response<?>> createBooking(@Valid @RequestBody CreateBookingRequest createBookingRequest) {
        return ResponseEntity.ok(bookingService.createBooking(createBookingRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<BookingDTO>> getBookingById(@RequestParam Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
    public ResponseEntity<Response<List<BookingDTO>>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/me")
    public ResponseEntity<Response<List<BookingDTO>>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PILOT')")
    public ResponseEntity<Response<?>> updateBooking(@PathVariable Long id,
                                                     @RequestBody BookingStatus bookingStatus) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, bookingStatus));
    }
}
