package com.teatro.backend.services.impl;

import com.teatro.backend.exceptions.ResourceNotFoundException;
import com.teatro.backend.models.dtos.EventDTO;
import com.teatro.backend.models.dtos.TicketOptionDTO;
import com.teatro.backend.models.entities.Event;
import com.teatro.backend.models.enums.EventStatus;
import com.teatro.backend.models.enums.EventType;
import com.teatro.backend.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        event = new Event();
        event.setId(1L);
        event.setTitle("Obra de teatro");
        event.setDescription("Comedia musical");
        event.setType(EventType.THEATER);
        event.setStartDateTime(LocalDateTime.now().plusDays(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(6));
        event.setStatus(EventStatus.SCHEDULED);

        eventDTO = new EventDTO();
        eventDTO.setId(1L);
        eventDTO.setTitle("Obra de teatro");
        eventDTO.setDescription("Comedia musical");
        eventDTO.setType(EventType.THEATER);
        eventDTO.setStartDateTime(event.getStartDateTime());
        eventDTO.setEndDateTime(event.getEndDateTime());
        eventDTO.setStatus(EventStatus.SCHEDULED);
    }

    @Test
    void shouldReturnAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);

        List<EventDTO> result = eventService.getAllEvents();

        assertEquals(1, result.size());
        assertEquals(EventType.THEATER, result.get(0).getType());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEventByIdWhenExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);

        EventDTO result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals(EventType.THEATER, result.getType());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.getEventById(99L));

        verify(eventRepository, times(1)).findById(99L);
    }

    @Test
    void shouldCreateEventWithTickets() {
        TicketOptionDTO ticketDTO = new TicketOptionDTO();
        ticketDTO.setName("VIP");
        ticketDTO.setPrice(new BigDecimal("1500.00"));
        ticketDTO.setCapacity(100);

        eventDTO.setTicketOptions(List.of(ticketDTO));

        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);

        EventDTO result = eventService.createEvent(eventDTO);

        assertEquals(EventType.THEATER, result.getType());
        assertEquals(EventStatus.SCHEDULED, result.getStatus());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void shouldUpdateExistingEvent() {
        eventDTO.setTitle("Obra actualizada");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);

        EventDTO result = eventService.updateEvent(1L, eventDTO);

        assertEquals("Obra actualizada", result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void shouldUpdateEventStatus() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);

        EventDTO result = eventService.updateEventStatus(1L, EventStatus.FINISHED);

        assertNotNull(result);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void shouldReturnOnlyUpcomingScheduledEvents() {
        Event pastEvent = new Event();
        pastEvent.setId(2L);
        pastEvent.setTitle("Recital pasado");
        pastEvent.setStatus(EventStatus.SCHEDULED);
        pastEvent.setStartDateTime(LocalDateTime.now().minusDays(1));
        pastEvent.setType(EventType.CONCERT);

        when(eventRepository.findByStatus(EventStatus.SCHEDULED))
                .thenReturn(List.of(event, pastEvent));
        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);

        List<EventDTO> result = eventService.getScheduledEvents();

        assertEquals(1, result.size());
        assertEquals(EventType.THEATER, result.get(0).getType());
        verify(eventRepository, times(1)).findByStatus(EventStatus.SCHEDULED);
    }
}
