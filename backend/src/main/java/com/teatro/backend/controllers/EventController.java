package com.teatro.backend.controllers;

import com.teatro.backend.models.dtos.EventDTO;
import com.teatro.backend.models.enums.EventStatus;
import com.teatro.backend.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDTO));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> getScheduledEvents() {
        return ResponseEntity.ok(eventService.getScheduledEvents());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EventDTO> updateEventStatus(
            @PathVariable Long id,
            @RequestParam EventStatus status
    ) {
        return ResponseEntity.ok(eventService.updateEventStatus(id, status));
    }
}