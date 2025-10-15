package com.teatro.backend.controllers;

import com.teatro.backend.models.dtos.CreateReservationRequest;
import com.teatro.backend.models.dtos.ReservationDTO;
import com.teatro.backend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private final ReservationService reservationService;


    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reservationService.getReservationsByCustomer(customerId));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(reservationService.updateReservationStatus(id, updates));
    }

}
