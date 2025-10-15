package com.teatro.backend.services.impl;

import com.teatro.backend.exceptions.ResourceNotFoundException;
import com.teatro.backend.models.dtos.CustomerDTO;
import com.teatro.backend.models.entities.Customer;
import com.teatro.backend.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Luca");
        customer.setLastName("Casamayor");
        customer.setEmail("luca@example.com");
        customer.setActive(true);
        customer.setLoyaltyFree(false);

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setFirstName("Luca");
        customerDTO.setLastName("Casamayor");
        customerDTO.setEmail("luca@example.com");
    }


    @Test
    void shouldReturnActiveCustomers() {
        when(customerRepository.findByActiveTrue()).thenReturn(List.of(customer));
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Luca", result.get(0).getFirstName());
        verify(customerRepository, times(1)).findByActiveTrue();
    }


    @Test
    void shouldReturnCustomerByIdWhenExists() {
        when(customerRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(customer));
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("Luca", result.getFirstName());
        verify(customerRepository, times(1)).findByIdAndActiveTrue(1L);
    }


    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(customerRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(99L));

        verify(customerRepository, times(1)).findByIdAndActiveTrue(99L);
    }

    // -------------------------------
    // ✅ TEST 4: createCustomer()
    // -------------------------------
    @Test
    void shouldCreateNewCustomerWithDefaults() {
        when(modelMapper.map(customerDTO, Customer.class)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertFalse(customer.getLoyaltyFree());
        assertTrue(customer.getActive());
        assertEquals("Luca", result.getFirstName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldUpdateExistingCustomer() {
        when(customerRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);

        CustomerDTO result = customerService.updateCustomer(1L, customerDTO);

        assertEquals("Luca", result.getFirstName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldSetCustomerInactiveWhenDeleting() {
        when(customerRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        assertFalse(customer.getActive());
        verify(customerRepository, times(1)).save(customer);
    }


    @Test
    void shouldReturnOnlyActiveCustomersWithFreePass() {
        customer.setLoyaltyFree(true);
        when(customerRepository.findByLoyaltyFreeTrue()).thenReturn(List.of(customer));
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);

        List<CustomerDTO> result = customerService.getCustomersWithFreePass();

        assertEquals(1, result.size());
        assertTrue(customer.getLoyaltyFree());
        verify(customerRepository, times(1)).findByLoyaltyFreeTrue();
    }
}
