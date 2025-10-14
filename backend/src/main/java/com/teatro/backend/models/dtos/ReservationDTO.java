package com.teatro.backend.models.dtos;

import com.teatro.backend.models.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long eventId;
    private String eventTitle;
    private ReservationStatus status;
    private Boolean loyaltyFree;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private List<ReservationItemDTO> items;
}

