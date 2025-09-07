package com.pm.flightsmanagement.repos;

import com.pm.flightsmanagement.entities.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailNotificationRepo extends JpaRepository<EmailNotification, Long> {
}
