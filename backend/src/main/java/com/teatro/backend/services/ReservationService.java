package com.teatro.backend.services;

import com.teatro.backend.models.dtos.CreateReservationRequest;
import com.teatro.backend.models.dtos.ReservationDTO;

import java.util.List;

public interface ReservationService {
    List<ReservationDTO> getAllReservations();
    ReservationDTO getReservationById(Long id);
    ReservationDTO createReservation(CreateReservationRequest request);
    ReservationDTO markAsPaid(Long id);
    void deleteReservation(Long id);
}
