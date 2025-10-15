package com.teatro.backend.services;

import com.teatro.backend.models.dtos.CreateReservationRequest;
import com.teatro.backend.models.dtos.ReservationDTO;

import java.util.List;
import java.util.Map;

public interface ReservationService {
    List<ReservationDTO> getAllReservations();
    ReservationDTO getReservationById(Long id);
    ReservationDTO createReservation(CreateReservationRequest request);
//    ReservationDTO markAsPaid(Long id);
    List<ReservationDTO> getReservationsByCustomer(Long customerId);
    void deleteReservation(Long id);

    ReservationDTO updateReservationStatus(Long id, Map<String, Object> updates);
}
