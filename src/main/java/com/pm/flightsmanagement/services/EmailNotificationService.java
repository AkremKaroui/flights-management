package com.pm.flightsmanagement.services;

import com.pm.flightsmanagement.entities.Booking;
import com.pm.flightsmanagement.entities.User;

public interface EmailNotificationService {

    void sendBookingTicketEmail(Booking booking);

    void sendWelcomeEmail(User user);
}
