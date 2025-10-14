package com.teatro.backend.repositories;

import com.teatro.backend.models.entities.Reservation;
import com.teatro.backend.models.enums.EventType;
import com.teatro.backend.models.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);
    List<Reservation> findByEventId(Long eventId);
    List<Reservation> findByStatus(ReservationStatus status);
    Long countByEvent_Type(EventType eventType);

    long countByCustomerIdAndStatusAndPaidAtBetween(
            Long customerId,
            ReservationStatus status,
            LocalDateTime from,
            LocalDateTime to
    );
    List<Reservation> findByActiveTrue();

    Optional<Reservation> findByIdAndActiveTrue(Long id);
}

