package com.teatro.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teatro.backend.models.dtos.CustomerDTO;
import com.teatro.backend.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    void shouldReturnAllCustomers() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFirstName("Luca");
        dto.setLastName("Casamayor");
        dto.setEmail("luca@example.com");
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setLoyaltyFree(false);
        dto.setActive(true);

        when(customerService.getAllCustomers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("Luca"))
                .andExpect(jsonPath("$[0].lastName").value("Casamayor"))
                .andExpect(jsonPath("$[0].email").value("luca@example.com"))
                .andExpect(jsonPath("$[0].loyaltyFree").value(false))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void shouldReturnCustomerById() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFirstName("Luca");
        dto.setLastName("Casamayor");

        when(customerService.getCustomerById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Luca"))
                .andExpect(jsonPath("$.lastName").value("Casamayor"));
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setFirstName("Luca");
        request.setLastName("Casamayor");
        request.setEmail("luca@example.com");

        CustomerDTO response = new CustomerDTO();
        response.setId(10L);
        response.setFirstName("Luca");
        response.setLastName("Casamayor");
        response.setEmail("luca@example.com");
        response.setLoyaltyFree(false);
        response.setActive(true);

        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.email").value("luca@example.com"))
                .andExpect(jsonPath("$.loyaltyFree").value(false))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setFirstName("Luca");
        request.setLastName("Casamayor");
        request.setEmail("luca@example.com");

        CustomerDTO updated = new CustomerDTO();
        updated.setId(1L);
        updated.setFirstName("Luca");
        updated.setLastName("Casamayor");
        updated.setEmail("luca@example.com");

        when(customerService.updateCustomer(Mockito.eq(1L), any(CustomerDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Luca"))
                .andExpect(jsonPath("$.email").value("luca@example.com"));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        Mockito.doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(patch("/api/customers/1/deactivate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnCustomersWithFreePass() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFirstName("Luca");
        dto.setLoyaltyFree(true);

        when(customerService.getCustomersWithFreePass()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/customers/free-pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].loyaltyFree").value(true));
    }
}
