package com.teatro.backend.repositories;

import com.teatro.backend.models.entities.Event;
import com.teatro.backend.models.enums.EventStatus;
import com.teatro.backend.models.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByType(EventType type);
}
