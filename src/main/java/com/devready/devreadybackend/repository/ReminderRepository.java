package com.devready.devreadybackend.repository;

import com.devready.devreadybackend.model.Reminder;
import com.devready.devreadybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // get all active (not dismissed) reminders for a specific user
    // shown on the dashboard to alert the user
    List<Reminder> findByUserAndDismissedFalse(User user);

    // get all reminders for a specific user regardless of dismissed status
    List<Reminder> findByUser(User user);
}