package com.teatro.backend.models.entities;

import com.teatro.backend.exceptions.InsufficientCapacityException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket_option")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotBlank(message = "Ticket name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer sold = 0;

    @Version
    private Integer version;

    public Integer getRemaining() {
        return capacity - sold;
    }

    public boolean isReservable(Integer quantity) {
        return getRemaining() >= quantity;
    }

    public void reserve(Integer quantity) {
        if (!isReservable(quantity)) {
            throw new InsufficientCapacityException(
                    "Not enough tickets available. Requested: " + quantity + ", Available: " + getRemaining()
            );
        }
        this.sold += quantity;
    }
}