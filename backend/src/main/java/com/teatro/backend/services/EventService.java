package com.teatro.backend.services;

import com.teatro.backend.models.dtos.EventDTO;
import com.teatro.backend.models.enums.EventStatus;

import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Long id);
    EventDTO createEvent(EventDTO eventDTO);
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    List<EventDTO> getScheduledEvents();
    EventDTO updateEventStatus(Long id, EventStatus status);
}
