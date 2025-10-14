package com.teatro.backend.models.entities;

import com.teatro.backend.models.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @NotNull(message = "Event is required")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    // (cliente)
    @Column(name = "attendee_name", nullable = false)
    private String attendeeName;

    // (empleado)
    @Column(name = "attended_by")
    private String attendedBy;

    @Column(name = "created_by_admin", nullable = false)
    private Boolean createdByAdmin = true;

    @Column(name = "loyalty_free", nullable = false)
    private Boolean loyaltyFree = false;

    @NotNull(message = "Total is required")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public BigDecimal calculateTotal() {
        if (loyaltyFree) {
            this.total = BigDecimal.ZERO;
        } else {
            this.total = items.stream()
                    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return this.total;
    }

    public void markAsPaid() {
        this.status = ReservationStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }
}

