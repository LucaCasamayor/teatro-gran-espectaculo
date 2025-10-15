package com.teatro.backend.services.impl;

import com.teatro.backend.exceptions.ResourceNotFoundException;
import com.teatro.backend.models.dtos.CreateReservationRequest;
import com.teatro.backend.models.dtos.ReservationDTO;
import com.teatro.backend.models.dtos.ReservationItemRequest;
import com.teatro.backend.models.entities.*;
import com.teatro.backend.models.enums.ReservationStatus;
import com.teatro.backend.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private EventRepository eventRepository;
    @Mock private TicketOptionRepository ticketOptionRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Customer customer;
    private Event event;
    private TicketOption ticketOption;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Luca");
        customer.setLastName("Casamayor");
        customer.setLoyaltyFree(false);
        customer.setActive(true);

        event = new Event();
        event.setId(1L);
        event.setTitle("Obra de teatro");

        ticketOption = mock(TicketOption.class);
        when(ticketOption.isReservable(anyInt())).thenReturn(true);
        when(ticketOption.getPrice()).thenReturn(new BigDecimal("100.00"));
        when(ticketOption.getName()).thenReturn("VIP");
        when(ticketOption.getId()).thenReturn(1L);
    }


    @Test
    void shouldCreateReservationSuccessfully() {

        ReservationItemRequest itemReq = new ReservationItemRequest();
        itemReq.setTicketOptionId(1L);
        itemReq.setQuantity(2);

        CreateReservationRequest req = new CreateReservationRequest();
        req.setCustomerId(1L);
        req.setEventId(1L);
        req.setAttendeeName("Juan Perez");
        req.setAttendedBy("Empleado");
        req.setItems(List.of(itemReq));


        when(customerRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(customer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketOptionRepository.findById(1L)).thenReturn(Optional.of(ticketOption));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        ReservationDTO result = reservationService.createReservation(req);


        assertNotNull(result);
        assertEquals("Obra de teatro", result.getEventTitle());
        verify(ticketOptionRepository, times(1)).save(ticketOption);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldThrowWhenTicketNotReservable() {
        when(customerRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(customer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketOptionRepository.findById(1L)).thenReturn(Optional.of(ticketOption));

        when(ticketOption.isReservable(anyInt())).thenReturn(false);

        ReservationItemRequest itemReq = new ReservationItemRequest();
        itemReq.setTicketOptionId(1L);
        itemReq.setQuantity(5);

        CreateReservationRequest req = new CreateReservationRequest();
        req.setCustomerId(1L);
        req.setEventId(1L);
        req.setItems(List.of(itemReq));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(req));
        verify(ticketOptionRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCustomerNotFound() {
        when(customerRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        CreateReservationRequest req = new CreateReservationRequest();
        req.setCustomerId(1L);
        req.setEventId(1L);

        assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(req));
        verify(reservationRepository, never()).save(any());
    }

//    @Test
//    void shouldMarkReservationAsPaidAndUpdateCustomer() {
//        Customer customer = new Customer();
//        customer.setId(10L);
//        customer.setFirstName("Luca");
//        customer.setLastName("Casamayor");
//        customer.setActive(true);
//        customer.setCurrentStreak(0);
//        customer.setTotalAttendances(0);
//        customer.setLoyaltyFree(false);
//        customer.setRegistrationDate(LocalDateTime.now());
//
//        Event event = new Event();
//        event.setId(20L);
//        event.setTitle("Obra de Teatro");
//
//        Reservation reservation = new Reservation();
//        reservation.setId(1L);
//        reservation.setCustomer(customer);
//        reservation.setEvent(event);
//        reservation.setStatus(ReservationStatus.PENDING);
//        reservation.setLoyaltyFree(false);
//        reservation.setItems(List.of());
//        reservation.setTotal(BigDecimal.ZERO);
//        reservation.setActive(true);
//
//        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
//        when(reservationRepository.save(any(Reservation.class)))
//                .thenAnswer(inv -> inv.getArgument(0));
//        when(customerRepository.save(any(Customer.class)))
//                .thenAnswer(inv -> inv.getArgument(0));
//
//        ReservationDTO dto = reservationService.markAsPaid(1L);
//
//        assertNotNull(dto);
//        assertEquals(ReservationStatus.PAID, reservation.getStatus());
//        verify(customerRepository, times(1)).save(any(Customer.class));
//        verify(reservationRepository, times(1)).save(any(Reservation.class));
//    }


//    @Test
//    void shouldThrowWhenReservationAlreadyPaid() {
//        Reservation reservation = new Reservation();
//        reservation.setId(1L);
//        reservation.setStatus(ReservationStatus.PAID);
//        reservation.setCustomer(customer);
//
//        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
//
//        assertThrows(IllegalStateException.class, () -> reservationService.markAsPaid(1L));
//    }

    @Test
    void shouldMarkReservationInactive() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setActive(true);

        when(reservationRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(reservation));

        reservationService.deleteReservation(1L);

        assertFalse(reservation.getActive());
        verify(reservationRepository, times(1)).save(reservation);
    }
}
