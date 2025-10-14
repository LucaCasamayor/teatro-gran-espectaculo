package com.teatro.backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationItemDTO {
    private Long id;
    private Long ticketOptionId;
    private String ticketOptionName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
