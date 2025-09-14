package com.pm.flightsmanagement.services.impl;

import com.pm.flightsmanagement.dtos.BookingDTO;
import com.pm.flightsmanagement.dtos.CreateBookingRequest;
import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.entities.Booking;
import com.pm.flightsmanagement.entities.Flight;
import com.pm.flightsmanagement.entities.Passenger;
import com.pm.flightsmanagement.entities.User;
import com.pm.flightsmanagement.enums.BookingStatus;
import com.pm.flightsmanagement.enums.FlightStatus;
import com.pm.flightsmanagement.exceptions.BadRequestException;
import com.pm.flightsmanagement.exceptions.NotFoundException;
import com.pm.flightsmanagement.repos.BookingRepo;
import com.pm.flightsmanagement.repos.FlightRepo;
import com.pm.flightsmanagement.repos.PassengerRepo;
import com.pm.flightsmanagement.services.BookingService;
import com.pm.flightsmanagement.services.EmailNotificationService;
import com.pm.flightsmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final UserService userService;
    private final FlightRepo flightRepo;
    private final ModelMapper modelMapper;
    private final PassengerRepo passengerRepo;
    private final EmailNotificationService emailNotificationService;

    @Override
    @Transactional
    public Response<?> createBooking(CreateBookingRequest createBookingRequest) {

        User currentUser = userService.currentUser();

        Flight flight = flightRepo.findById(createBookingRequest.getFlightID())
                .orElseThrow(() -> new NotFoundException("Flight doesn't exists"));

        if (flight.getStatus() != FlightStatus.SCHEDULED) {
            throw new BadRequestException("You can only book a scheduled flight");
        }

        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUser(currentUser);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepo.save(booking);

        if (createBookingRequest.getPassengers() != null && !createBookingRequest.getPassengers().isEmpty()) {
            List<Passenger> passengers = createBookingRequest.getPassengers().stream()
                    .map(passengerDTO -> {
                        Passenger passenger = modelMapper.map(passengerDTO, Passenger.class);
                        passenger.setBooking(savedBooking);
                        return passenger;
                    })
                    .toList();

            passengerRepo.saveAll(passengers);
        }

        emailNotificationService.sendBookingTicketEmail(savedBooking);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking created")
                .build();

    }

    @Override
    public Response<BookingDTO> getBookingById(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking doesn't exists"));
        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        bookingDTO.getFlight().setBookings(null);
        return Response.<BookingDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("booking data")
                .data(bookingDTO)
                .build();
    }

    @Override
    public Response<List<BookingDTO>> getAllBookings() {
        List<Booking> allBookings = bookingRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<BookingDTO> bookingDTOS = allBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
                    bookingDTO.getFlight().setBookings(null);
                    return bookingDTO;
                }).toList();

        return Response.<List<BookingDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("all bookings")
                .data(bookingDTOS)
                .build();
    }

    @Override
    public Response<List<BookingDTO>> getMyBookings() {
        User user = userService.currentUser();
        List<Booking> myBookings = bookingRepo.findByUserIdOrderByIdDesc(user.getId());
        List<BookingDTO> myBookingDtos = myBookings.stream()
                .map(booking -> {
                    BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
                    bookingDTO.getFlight().setBookings(null);
                    return bookingDTO;
                }).toList();

        return Response.<List<BookingDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("all MY bookings")
                .data(myBookingDtos)
                .build();
    }

    @Override
    public Response<?> updateBookingStatus(Long id, BookingStatus bookingStatus) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking doesn't exists"));

        booking.setStatus(bookingStatus);
        bookingRepo.save(booking);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Booking status updated")
                .build();
    }

    private String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
