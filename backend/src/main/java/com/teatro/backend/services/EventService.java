package com.teatro.backend.services;

import com.teatro.backend.models.dtos.EventDTO;

import java.util.List;

public interface EventService {
    List<EventDTO> getAllEvents();
    EventDTO getEventById(Long id);
    EventDTO createEvent(EventDTO eventDTO);
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    void cancelEvent(Long id);
    List<EventDTO> getScheduledEvents();
}
