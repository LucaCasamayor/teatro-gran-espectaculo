package com.teatro.backend.services.impl;

import com.teatro.backend.exceptions.ResourceNotFoundException;
import com.teatro.backend.models.dtos.CreateReservationRequest;
import com.teatro.backend.models.dtos.ReservationDTO;
import com.teatro.backend.models.dtos.ReservationItemRequest;
import com.teatro.backend.models.entities.*;
import com.teatro.backend.models.enums.ReservationStatus;
import com.teatro.backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Test
    void shouldUpdateReservationStatusToPaid() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Luca");
        customer.setLastName("Casamayor");

        Event event = new Event();
        event.setId(10L);
        event.setTitle("Concierto de Rock");

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPaidAt(null);
        reservation.setCustomer(customer);
        reservation.setEvent(event);
        reservation.setItems(List.of());

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> updates = Map.of(
                "status", "PAID",
                "paidAt", true
        );

        ReservationDTO result = reservationService.updateReservationStatus(1L, updates);

        assertNotNull(result);
        assertEquals(ReservationStatus.PAID, reservation.getStatus());
        assertNotNull(reservation.getPaidAt());
        verify(reservationRepository, times(1)).save(reservation);
    }


    @Test
    void shouldThrowWhenReservationNotFoundOnUpdateStatus() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                reservationService.updateReservationStatus(99L, Map.of("status", "PAID"))
        );
    }
    @Test
    void shouldReturnAllActiveReservations() {
        // --- Arrange ---
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Luca");
        customer.setLastName("Casamayor");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Concierto");

        Reservation activeReservation = new Reservation();
        activeReservation.setId(1L);
        activeReservation.setCustomer(customer);
        activeReservation.setEvent(event);
        activeReservation.setStatus(ReservationStatus.PAID);
        activeReservation.setActive(true);
        activeReservation.setItems(List.of());

        Reservation inactiveReservation = new Reservation();
        inactiveReservation.setId(2L);
        inactiveReservation.setCustomer(customer);
        inactiveReservation.setEvent(event);
        inactiveReservation.setActive(false);
        inactiveReservation.setItems(List.of());

        when(reservationRepository.findAll()).thenReturn(List.of(activeReservation, inactiveReservation));

        // --- Act ---
        List<ReservationDTO> result = reservationService.getAllReservations();

        // --- Assert ---
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Concierto", result.get(0).getEventTitle());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnReservationByIdIfActive() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Luca");
        customer.setLastName("Casamayor");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Teatro");

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCustomer(customer);
        reservation.setEvent(event);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setActive(true);
        reservation.setItems(List.of());

        when(reservationRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(reservation));

        ReservationDTO dto = reservationService.getReservationById(1L);

        assertNotNull(dto);
        assertEquals("Teatro", dto.getEventTitle());
        assertEquals("Luca Casamayor", dto.getCustomerName());
        verify(reservationRepository, times(1)).findByIdAndActiveTrue(1L);
    }

    @Test
    void shouldThrowWhenReservationNotFoundById() {
        when(reservationRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservationService.getReservationById(1L));
    }

    @Test
    void shouldReturnReservationsByCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Luca");
        customer.setLastName("Casamayor");
        customer.setActive(true);

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Concierto");

        Reservation reservation = new Reservation();
        reservation.setId(10L);
        reservation.setCustomer(customer);
        reservation.setEvent(event);
        reservation.setActive(true);
        reservation.setItems(List.of());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(reservationRepository.findByCustomerId(1L)).thenReturn(List.of(reservation));

        List<ReservationDTO> result = reservationService.getReservationsByCustomer(1L);

        assertEquals(1, result.size());
        assertEquals("Concierto", result.get(0).getEventTitle());
        assertEquals("Luca Casamayor", result.get(0).getCustomerName());
        verify(customerRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void shouldThrowWhenCustomerNotFoundOnGetReservationsByCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservationService.getReservationsByCustomer(1L));
        verify(reservationRepository, never()).findByCustomerId(anyLong());
    }

}
