package com.teatro.backend.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "total_attendances", nullable = false)
    private Integer totalAttendances = 0;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Column(name = "loyalty_free", nullable = false)
    private Boolean loyaltyFree = false;

    @Column(nullable = false)
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }

    public void incrementAttendance() {
        int currentYear = LocalDate.now().getYear();
        if (registrationDate.getYear() < currentYear) {

            this.currentStreak = 0;
            this.totalAttendances = 0;
            this.loyaltyFree = false;
            this.registrationDate = LocalDateTime.now();
        }

        this.totalAttendances++;
        this.currentStreak++;

        if (this.currentStreak >= 5) {
            this.loyaltyFree = true;
        }
    }


    public void resetStreak() {
        this.currentStreak = 0;
        this.loyaltyFree = false;
    }
}


