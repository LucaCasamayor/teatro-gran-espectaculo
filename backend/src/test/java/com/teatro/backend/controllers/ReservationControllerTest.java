package com.teatro.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teatro.backend.models.dtos.CreateReservationRequest;
import com.teatro.backend.models.dtos.ReservationDTO;
import com.teatro.backend.models.enums.ReservationStatus;
import com.teatro.backend.services.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @Test
    void shouldReturnAllReservations() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(1L);
        dto.setEventTitle("Concierto");
        dto.setStatus(ReservationStatus.PAID);
        dto.setTotal(BigDecimal.valueOf(1200));

        when(reservationService.getAllReservations()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].eventTitle").value("Concierto"))
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[0].total").value(1200));
    }

    @Test
    void shouldReturnReservationById() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(1L);
        dto.setEventTitle("Obra de teatro");
        dto.setStatus(ReservationStatus.PENDING);

        when(reservationService.getReservationById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.eventTitle").value("Obra de teatro"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldCreateReservation() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setCustomerId(10L);
        request.setEventId(5L);
        request.setAttendeeName("Luca");

        ReservationDTO response = new ReservationDTO();
        response.setId(99L);
        response.setCustomerName("Luca Casamayor");
        response.setStatus(ReservationStatus.PENDING);

        when(reservationService.createReservation(any(CreateReservationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.customerName").value("Luca Casamayor"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

//    @Test
//    void shouldMarkReservationAsPaid() throws Exception {
//        ReservationDTO dto = new ReservationDTO();
//        dto.setId(1L);
//        dto.setStatus(ReservationStatus.PAID);
//        dto.setPaidAt(LocalDateTime.now());
//
//        when(reservationService.markAsPaid(1L)).thenReturn(dto);
//
//        mockMvc.perform(patch("/api/reservations/1/pay"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("PAID"));
//    }

    @Test
    void shouldDeleteReservation() throws Exception {
        Mockito.doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnReservationsByCustomer() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(1L);
        dto.setCustomerName("Luca Casamayor");
        dto.setStatus(ReservationStatus.PAID);

        when(reservationService.getReservationsByCustomer(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/reservations/customer/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Luca Casamayor"))
                .andExpect(jsonPath("$[0].status").value("PAID"));
    }
}
