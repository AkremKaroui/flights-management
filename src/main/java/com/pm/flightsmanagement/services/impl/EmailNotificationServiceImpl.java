package com.pm.flightsmanagement.services.impl;

import com.pm.flightsmanagement.entities.Booking;
import com.pm.flightsmanagement.entities.EmailNotification;
import com.pm.flightsmanagement.entities.User;
import com.pm.flightsmanagement.repos.EmailNotificationRepo;
import com.pm.flightsmanagement.services.EmailNotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final EmailNotificationRepo emailNotificationRepo;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${viewBookingUrl}")
    private String viewBookingUrl;

    @Value("${frontEndLoginUrl}")
    private String frontendLoginUrl;

    @Override
    @Transactional
    @Async
    public void sendBookingTicketEmail(Booking booking) {
        log.info("Sending Booking Email");
        String recipientEmail = booking.getUser().getEmail();
        String subject =
                "Your Booking Ticket Confirmed - Reference " + booking.getBookingReference();
        String templateName = "booking_confirmed";

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("userName", booking.getUser().getName());
        templateVariables.put("bookingReference", booking.getBookingReference());
        templateVariables.put("flightNumber", booking.getFlight().getFlightNumber());
        templateVariables.put("departureAirportIataCode",
                booking.getFlight().getDepartureAirport().getIataCode());
        templateVariables.put("departureAirportName",
                booking.getFlight().getDepartureAirport().getName());
        templateVariables.put("departureAirportCity",
                booking.getFlight().getDepartureAirport().getCity());
        templateVariables.put("departureTime", booking.getFlight().getDepartureTime());
        templateVariables.put("arrivalAirportIataCode",
                booking.getFlight().getArrivalAirport().getIataCode());
        templateVariables.put("arrivalAirportName",
                booking.getFlight().getArrivalAirport().getName());
        templateVariables.put("arrivalAirportCity",
                booking.getFlight().getArrivalAirport().getCity());
        templateVariables.put("arrivalTime", booking.getFlight().getArrivalTime());
        templateVariables.put("basePrice", booking.getFlight().getBasePrice());
        templateVariables.put("passengers", booking.getPassengers());
        templateVariables.put("viewBookingUrl", viewBookingUrl);

        Context context = new Context();
        templateVariables.forEach(context::setVariable);
        String emailBody = templateEngine.process(templateName, context);

        sendMail(recipientEmail, subject, emailBody, true, booking);

    }

    @Override
    public void sendWelcomeEmail(User user) {
        log.info("Sending welcome email to user: {}", user.getEmail());

        String recipientEmail = user.getEmail();
        String subject = "Welcome to Airline Management!";
        String templateName = "welcome_user"; // Hardcoded template name for internal use

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("userName", user.getName());
        templateVariables.put("frontendLoginUrl", frontendLoginUrl);

        // Render the template content
        Context context = new Context();
        templateVariables.forEach(context::setVariable);
        String emailBody = templateEngine.process(templateName, context);

        sendMail(recipientEmail, subject, emailBody, true, null);

    }

    private void sendMail(String recipientEmail, String subject, String body, boolean isHtml,
                          Booking booking) {
        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, isHtml);

            log.info("About to send Email...");
            javaMailSender.send(mimeMessage);

            log.info("Email sent out ");

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        //save to the notification database table

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setRecipientEmail(recipientEmail);
        emailNotification.setSubject(subject);
        emailNotification.setBody(body);
        emailNotification.setHtml(isHtml);
        emailNotification.setSentAt(LocalDateTime.now());
        emailNotification.setBooking(booking);

        emailNotificationRepo.save(emailNotification);
    }
}
