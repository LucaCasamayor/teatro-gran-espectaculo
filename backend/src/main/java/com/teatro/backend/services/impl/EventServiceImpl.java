package com.teatro.backend.services.impl;

import com.teatro.backend.exceptions.ResourceNotFoundException;
import com.teatro.backend.models.dtos.EventDTO;
import com.teatro.backend.models.dtos.TicketOptionDTO;
import com.teatro.backend.models.entities.Event;
import com.teatro.backend.models.entities.TicketOption;
import com.teatro.backend.models.enums.EventStatus;
import com.teatro.backend.repositories.EventRepository;
import com.teatro.backend.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToDTO(event);
    }


    @Override
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setType(eventDTO.getType());
        event.setDescription(eventDTO.getDescription());
        event.setStartDateTime(eventDTO.getStartDateTime());
        event.setEndDateTime(eventDTO.getEndDateTime());
        event.setStatus(EventStatus.SCHEDULED);

        if (eventDTO.getTicketOptions() != null) {
            for (TicketOptionDTO ticketDTO : eventDTO.getTicketOptions()) {
                TicketOption ticket = new TicketOption();
                ticket.setName(ticketDTO.getName());
                ticket.setPrice(ticketDTO.getPrice());
                ticket.setCapacity(ticketDTO.getCapacity());
                ticket.setSold(0);
                ticket.setEvent(event);
                event.getTicketOptions().add(ticket);
            }
        }

        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setType(eventDTO.getType());
        event.setStartDateTime(eventDTO.getStartDateTime());
        event.setEndDateTime(eventDTO.getEndDateTime());


        if (eventDTO.getStatus() != null) {
            event.setStatus(eventDTO.getStatus());
        }

        // logica para actualizar las opciones de entradas existentes
        if (eventDTO.getTicketOptions() != null) {
            for (TicketOptionDTO ticketDTO : eventDTO.getTicketOptions()) {
                event.getTicketOptions().stream()
                        .filter(t -> t.getId().equals(ticketDTO.getId()))
                        .findFirst()
                        .ifPresent(ticket -> {
                            ticket.setName(ticketDTO.getName());
                            ticket.setPrice(ticketDTO.getPrice());
                            ticket.setCapacity(ticketDTO.getCapacity());
                        });
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }

    @Override
    @Transactional
    public EventDTO updateEventStatus(Long id, EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        event.setStatus(status);
        Event updatedEvent = eventRepository.save(event);

        return modelMapper.map(updatedEvent, EventDTO.class);
    }



    @Override
    public List<EventDTO> getScheduledEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByStatus(EventStatus.SCHEDULED).stream()
                .filter(event -> event.getStartDateTime().isAfter(now))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Usé convertToDTO() en lugar de modelMapper.map() porque el DTO incluye
    // campos calculados, como la disponibilidad de entradas que no existen en la entidad.
    private EventDTO convertToDTO(Event event) {
        EventDTO dto = modelMapper.map(event, EventDTO.class);

        if (event.getTicketOptions() != null) {
            List<TicketOptionDTO> ticketDTOs = event.getTicketOptions().stream()
                    .map(ticket -> {
                        TicketOptionDTO ticketDTO = modelMapper.map(ticket, TicketOptionDTO.class);
                        ticketDTO.setAvailable(ticket.getCapacity() - ticket.getSold());
                        return ticketDTO;
                    })
                    .collect(Collectors.toList());
            dto.setTicketOptions(ticketDTOs);
        }

        return dto;
    }
}
