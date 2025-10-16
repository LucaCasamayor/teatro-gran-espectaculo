package com.teatro.backend.services.impl;

import com.teatro.backend.exceptions.ResourceNotFoundException;
import com.teatro.backend.models.dtos.CreateReservationRequest;
import com.teatro.backend.models.dtos.ReservationDTO;
import com.teatro.backend.models.dtos.ReservationItemDTO;
import com.teatro.backend.models.dtos.ReservationItemRequest;
import com.teatro.backend.models.entities.*;
import com.teatro.backend.models.enums.ReservationStatus;
import com.teatro.backend.repositories.CustomerRepository;
import com.teatro.backend.repositories.EventRepository;
import com.teatro.backend.repositories.ReservationRepository;
import com.teatro.backend.repositories.TicketOptionRepository;
import com.teatro.backend.services.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final EventRepository eventRepository;
    private final TicketOptionRepository ticketOptionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .filter(Reservation::getActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return convertToDTO(reservation);
    }


    @Override
    @Transactional
    public ReservationDTO createReservation(CreateReservationRequest request) {

        Customer customer = customerRepository.findByIdAndActiveTrue(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.getEventId()));


        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setEvent(event);
        reservation.setAttendeeName(request.getAttendeeName());
        reservation.setAttendedBy(request.getAttendedBy());
        reservation.setCreatedByAdmin(true);
        reservation.setLoyaltyFree(customer.getLoyaltyFree());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setActive(true);

        List<ReservationItem> items = new ArrayList<>();
        boolean freeUsed = false;


        for (ReservationItemRequest itemRequest : request.getItems()) {
            TicketOption ticketOption = ticketOptionRepository.findById(itemRequest.getTicketOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket option not found"));

            if (!ticketOption.isReservable(itemRequest.getQuantity())) {
                throw new IllegalStateException("Not enough tickets available for " + ticketOption.getName());
            }

            // Reservar las entradas (reduce capacidad disponible)
            ticketOption.reserve(itemRequest.getQuantity());
            ticketOptionRepository.save(ticketOption);

            ReservationItem item = new ReservationItem();
            item.setReservation(reservation);
            item.setTicketOption(ticketOption);


            if (customer.getLoyaltyFree() && !freeUsed) {

                int totalQty = itemRequest.getQuantity();

                // compró solo 1 entrada
                if (totalQty == 1) {
                    item.setQuantity(1);
                    item.setUnitPrice(BigDecimal.ZERO);
                    items.add(item);
                }

                // compró varias → 1 gratis + (n-1) pagas
                else {
                    // Ítem gratis
                    ReservationItem freeItem = new ReservationItem();
                    freeItem.setReservation(reservation);
                    freeItem.setTicketOption(ticketOption);
                    freeItem.setQuantity(1);
                    freeItem.setUnitPrice(BigDecimal.ZERO);
                    items.add(freeItem);

                    // Ítem con precio normal (las restantes)
                    item.setQuantity(totalQty - 1);
                    item.setUnitPrice(ticketOption.getPrice());
                    items.add(item);
                }


                freeUsed = true;
                customer.setLoyaltyFree(false);
            } else {

                //  precio normal
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(ticketOption.getPrice());
                items.add(item);
            }
        }


        customerRepository.save(customer);
        reservation.setItems(items);
        reservation.calculateTotal();
        Reservation saved = reservationRepository.save(reservation);

        return convertToDTO(saved);
    }



    @Override
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByCustomer(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));


        List<Reservation> reservations = reservationRepository.findByCustomerId(customerId)
                .stream()
                .filter(Reservation::getActive)
                .toList();

        return reservations.stream()
                .map(this::convertToDTO)
                .toList();
    }


    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        reservation.setActive(false);
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public ReservationDTO updateReservationStatus(Long id, Map<String, Object> updates) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));

        if (reservation.getStatus() == ReservationStatus.PAID) {
            throw new IllegalStateException("You cannot modify a reservation that is already paid");
        }

        Object statusValue = updates.get("status");
        if (statusValue == null) {
            throw new IllegalArgumentException("Missing 'status' field in request body");
        }

        String newStatus = statusValue.toString().trim().toUpperCase();

        ReservationStatus statusEnum;
        try {
            statusEnum = ReservationStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }

        reservation.setStatus(statusEnum);

        if (statusEnum == ReservationStatus.PAID) {
            reservation.setPaidAt(LocalDateTime.now());
        } else if (statusEnum == ReservationStatus.CANCELLED) {
            reservation.setPaidAt(null);
        }

        reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }

    @Override
    @Transactional
    public ReservationDTO updateReservation(Long id, ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));

        if (reservation.getStatus() == ReservationStatus.PAID) {
            throw new IllegalStateException("You cannot modify a reservation that is already paid");
        }


        reservation.setAttendeeName(dto.getAttendeeName());
        reservation.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found")));
        reservation.setEvent(eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found")));


        reservation.getItems().clear();
        for (ReservationItemDTO itemDto : dto.getItems()) {
            TicketOption option = ticketOptionRepository.findById(itemDto.getTicketOptionId())
                    .orElseThrow(() -> new EntityNotFoundException("Ticket option not found"));
            ReservationItem item = new ReservationItem();
            item.setReservation(reservation);
            item.setTicketOption(option);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(option.getPrice());
            reservation.getItems().add(item);
        }
        reservation.calculateTotal();
        reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }




    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setCustomerId(reservation.getCustomer().getId());
        dto.setCustomerName(
                reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName()
        );
        dto.setEventId(reservation.getEvent().getId());
        dto.setEventTitle(reservation.getEvent().getTitle());
        dto.setStatus(reservation.getStatus());
        dto.setLoyaltyFree(reservation.getLoyaltyFree());
        dto.setTotal(reservation.getTotal());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setPaidAt(reservation.getPaidAt());
        dto.setAttendeeName(reservation.getAttendeeName());
        dto.setAttendedBy(reservation.getAttendedBy());

        List<ReservationItemDTO> itemDTOs = reservation.getItems().stream()
                .map(item -> new ReservationItemDTO(
                        item.getId(),
                        item.getTicketOption().getId(),
                        item.getTicketOption().getName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}
