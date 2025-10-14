package com.teatro.backend.repositories;

import com.teatro.backend.models.entities.TicketOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketOptionRepository extends JpaRepository<TicketOption, Long> {
    List<TicketOption> findByEventId(Long eventId);
}

