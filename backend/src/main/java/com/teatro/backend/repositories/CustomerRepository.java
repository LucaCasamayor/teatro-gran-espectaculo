package com.teatro.backend.repositories;

import com.teatro.backend.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findByLoyaltyFreeTrue();
    Long countByLoyaltyFreeTrue();
    List<Customer> findByActiveTrue();
    Optional<Customer> findByIdAndActiveTrue(Long id);
}
