package com.pm.flightsmanagement.services;

import com.pm.flightsmanagement.dtos.BookingDTO;
import com.pm.flightsmanagement.dtos.CreateBookingRequest;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    Response<?> createBooking(CreateBookingRequest createBookingRequest);

    Response<BookingDTO> getBookingById(Long id);

    Response<List<BookingDTO>> getAllBookings();

    Response<List<BookingDTO>> getMyBookings();

    Response<?> updateBookingStatus(Long id, BookingStatus bookingStatus);
}
