package com.teatro.backend.repositories;

import com.teatro.backend.models.entities.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {
}
