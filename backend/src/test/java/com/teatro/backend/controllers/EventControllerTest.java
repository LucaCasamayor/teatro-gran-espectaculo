package com.teatro.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teatro.backend.models.dtos.EventDTO;
import com.teatro.backend.models.enums.EventStatus;
import com.teatro.backend.models.enums.EventType;
import com.teatro.backend.services.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void shouldReturnAllEvents() throws Exception {
        EventDTO dto = new EventDTO();
        dto.setId(1L);
        dto.setTitle("Obra de Teatro");
        dto.setType(EventType.THEATER);
        dto.setStartDateTime(LocalDateTime.of(2025, 10, 30, 20, 0));
        dto.setEndDateTime(LocalDateTime.of(2025, 10, 30, 22, 0));
        dto.setStatus(EventStatus.SCHEDULED);
        dto.setDescription("Una gran comedia clásica");

        when(eventService.getAllEvents()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Obra de Teatro"))
                .andExpect(jsonPath("$[0].type").value("THEATER"))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    @Test
    void shouldReturnEventById() throws Exception {
        EventDTO dto = new EventDTO();
        dto.setId(1L);
        dto.setTitle("Concierto Rock");
        dto.setType(EventType.CONCERT);
        dto.setStatus(EventStatus.SCHEDULED);

        when(eventService.getEventById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Concierto Rock"))
                .andExpect(jsonPath("$.type").value("CONCERT"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void shouldCreateEvent() throws Exception {
        EventDTO request = new EventDTO();
        request.setTitle("Conferencia Tech");
        request.setType(EventType.CONFERENCE);
        request.setStartDateTime(LocalDateTime.of(2025, 11, 5, 18, 0));
        request.setEndDateTime(LocalDateTime.of(2025, 11, 5, 21, 0));
        request.setStatus(EventStatus.SCHEDULED);
        request.setDescription("Charla sobre inteligencia artificial");

        EventDTO response = new EventDTO();
        response.setId(10L);
        response.setTitle("Conferencia Tech");
        response.setType(EventType.CONFERENCE);
        response.setStartDateTime(request.getStartDateTime());
        response.setEndDateTime(request.getEndDateTime());
        response.setStatus(EventStatus.SCHEDULED);
        response.setDescription(request.getDescription());

        when(eventService.createEvent(any(EventDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Conferencia Tech"))
                .andExpect(jsonPath("$.type").value("CONFERENCE"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        EventDTO request = new EventDTO();
        request.setTitle("Obra Dramática");
        request.setType(EventType.THEATER);
        request.setDescription("Nueva versión de Hamlet");
        request.setStartDateTime(LocalDateTime.of(2025, 12, 1, 20, 0));
        request.setEndDateTime(LocalDateTime.of(2025, 12, 1, 22, 30));
        request.setStatus(EventStatus.SCHEDULED);

        EventDTO updated = new EventDTO();
        updated.setId(5L);
        updated.setTitle("Obra Dramática");
        updated.setType(EventType.THEATER);
        updated.setDescription("Nueva versión de Hamlet");
        updated.setStartDateTime(request.getStartDateTime());
        updated.setEndDateTime(request.getEndDateTime());
        updated.setStatus(EventStatus.SCHEDULED);

        when(eventService.updateEvent(Mockito.eq(5L), any(EventDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/events/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.title").value("Obra Dramática"))
                .andExpect(jsonPath("$.type").value("THEATER"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void shouldReturnScheduledEvents() throws Exception {
        EventDTO dto = new EventDTO();
        dto.setId(1L);
        dto.setTitle("Festival de Música");
        dto.setType(EventType.CONCERT);
        dto.setStatus(EventStatus.SCHEDULED);

        when(eventService.getScheduledEvents()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value("CONCERT"))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    @Test
    void shouldUpdateEventStatus() throws Exception {
        EventDTO dto = new EventDTO();
        dto.setId(3L);
        dto.setTitle("Concierto de Jazz");
        dto.setType(EventType.CONCERT);
        dto.setStatus(EventStatus.FINISHED);

        when(eventService.updateEventStatus(3L, EventStatus.FINISHED)).thenReturn(dto);

        mockMvc.perform(patch("/api/events/3/status")
                        .param("status", "FINISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.status").value("FINISHED"));
    }
}
